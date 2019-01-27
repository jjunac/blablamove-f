package fr.unice.polytech.al.teamf.components;

import fr.unice.polytech.al.teamf.AnswerPackageHosting;
import fr.unice.polytech.al.teamf.FindPackageHost;
import fr.unice.polytech.al.teamf.NotifyUser;
import fr.unice.polytech.al.teamf.entities.*;
import fr.unice.polytech.al.teamf.notifier.Notifier;
import fr.unice.polytech.al.teamf.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class TemporaryLocationBean implements FindPackageHost, AnswerPackageHosting {

    RabbitTemplate rabbitTemplate;
    private Notifier notifier = Notifier.getInstance();

    @Autowired
    NotifyUser notifyUser;
    @Autowired
    UserRepository userRepository;

    public TemporaryLocationBean(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public User findHost(Parcel parcel) {
        log.trace("FindPackageHostBean.findHost");
        // Mocking new Host user
        User newHost = userRepository.findByName("Julien").get(0);

        Map<String, Serializable> parameters = new HashMap<>();
        parameters.put("parcelId", parcel.getId());
        parameters.put("username", newHost.getName());
        notifier.sendNotification(newHost, buildHostMessage(parcel.getOwner().getName()), true, rabbitTemplate);
//        notifyUser.notifyUserWithAnswer(newHost, buildHostMessage(parcel.getOwner().getName()),
//                new Answer("/package", "answerToPendingPackageHosting", parameters));

        return newHost;
    }

    @Override
    public boolean answerToPendingPackageHosting(Parcel parcel, User user, boolean answer) {
        if(answer) {
//            notifyUser.notifyUser(parcel.getOwner(), buildOwnerMessage(user.getName()));
//            notifyUser.notifyUser(parcel.getKeeper(), buildKeeperMessage(user.getName(), parcel.getOwner().getName()));
            notifier.sendNotification(parcel.getOwner(), buildOwnerMessage(user.getName()), false, rabbitTemplate);
            notifier.sendNotification(parcel.getKeeper(), buildKeeperMessage(user.getName(), parcel.getOwner().getName()), false, rabbitTemplate);
        }
        // FIXME handle error case
        return true;
    }

    static String buildOwnerMessage(String hostName) {
        return String.format("%s will host your package until a new transporter arrives !", hostName);
    }

    static String buildHostMessage(String hostName) {
        return String.format("Could you please host %s's package ?", hostName);
    }

    static String buildKeeperMessage(String hostName, String keeperName) {
        return String.format("%s will host %s package !", hostName, keeperName);
    }
}
