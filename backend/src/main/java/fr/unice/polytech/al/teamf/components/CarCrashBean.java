package fr.unice.polytech.al.teamf.components;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.unice.polytech.al.teamf.FindDriver;
import fr.unice.polytech.al.teamf.NotifyCarCrash;
import fr.unice.polytech.al.teamf.entities.*;
import fr.unice.polytech.al.teamf.notifier.Notifier;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@Component
public class CarCrashBean implements NotifyCarCrash {
    
    String insurance_url = "http://insurance:5000";

    private Notifier notifier = Notifier.getInstance();

    RabbitTemplate rabbitTemplate;

    public CarCrashBean(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Autowired
    FindDriver findDriver;
    @Autowired
    AccountingBean accountingBean;

    /**
     * @param user User transporting the packages
     */
    @Override
    @Transactional
    public void notifyCrash(User user, GPSCoordinate coordinate) {
        log.trace("NotifyCarCrashBean.notifyCrash");
        contactInsurance(user);
        for (Mission mission : user.getTransportedMissionsWithStatus(Mission.Status.ONGOING)) {
            accountingBean.computePoints(mission);
            notifier.sendNotification(mission.getOwner(),new Notification(mission.getOwner(),buildMessage(user.getName()),null),  rabbitTemplate);
            Parcel parcel = mission.getParcel();
            if (parcel!=null) {
                parcel.setMission(null);
                mission.setParcel(null);
                findDriver.findNewDriver(user, parcel, coordinate, mission.getArrival());
            }
        }
    }

    boolean contactInsurance(User user) {
        String jsonContent = new ObjectMapper()
                .createObjectNode()
                .put("user", user.getName())
                .toString();
        rabbitTemplate.convertAndSend("insurance", jsonContent);
        return true; // handle this properly
    }

    static String buildMessage(String username) {
        return String.format("%s had an accident while transporting your package !", username);
    }

}
