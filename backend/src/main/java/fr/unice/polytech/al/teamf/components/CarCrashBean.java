package fr.unice.polytech.al.teamf.components;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.unice.polytech.al.teamf.NotifyCarCrash;
import fr.unice.polytech.al.teamf.FindDriver;
import fr.unice.polytech.al.teamf.NotifyUser;
import fr.unice.polytech.al.teamf.entities.GPSCoordinate;
import fr.unice.polytech.al.teamf.entities.Mission;
import fr.unice.polytech.al.teamf.entities.Parcel;
import fr.unice.polytech.al.teamf.entities.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Slf4j
@Component
public class CarCrashBean implements NotifyCarCrash {

    String insurance_url = "http://insurance:5000";

    RabbitTemplate rabbitTemplate;

    public CarCrashBean(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Autowired
    NotifyUser notifyUser;
    @Autowired
    FindDriver findDriver;
    @Autowired
    AccountingBean accountingBean;

    /**
     * @param user User transporting the packages
     */
    @Override
    public void notifyCrash(User user, GPSCoordinate coordinate) {
        log.trace("NotifyCarCrashBean.notifyCrash");
        contactInsurance(user);
        for (Mission mission : user.getTransportedMissionsWithStatus(Mission.Status.ONGOING)) {
            accountingBean.computePoints(mission);
            notifyUser.notifyUser(mission.getOwner(), buildMessage(user.getName()));
            Parcel parcel = mission.getParcel();
            parcel.setMission(null);
            mission.setParcel(null);
            findDriver.findNewDriver(user, parcel, coordinate, mission.getArrival());
        }
    }

    boolean contactInsurance(User user) {
        String jsonContent = new ObjectMapper()
                .createObjectNode()
                .put("user", user.getName())
                .toString();
        rabbitTemplate.convertAndSend("insurance-exchange", "insurance.involvement", jsonContent);
        return false; // handle this properly
    }

    static String buildMessage(String username) {
        return String.format("%s had an accident while transporting your package !", username);
    }

}
