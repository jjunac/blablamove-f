package fr.unice.polytech.al.teamf.components;

import fr.unice.polytech.al.teamf.NotifyCarCrash;
import fr.unice.polytech.al.teamf.FindDriver;
import fr.unice.polytech.al.teamf.NotifyUser;
import fr.unice.polytech.al.teamf.entities.GPSCoordinate;
import fr.unice.polytech.al.teamf.entities.Mission;
import fr.unice.polytech.al.teamf.entities.Parcel;
import fr.unice.polytech.al.teamf.entities.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class NotifyCarCrashBean implements NotifyCarCrash {

    private final Logger logger = LoggerFactory.getLogger(NotifyCarCrashBean.class);

    @Autowired
    NotifyUser notifyUser;

    @Autowired
    FindDriver findDriver;

    /**
     *
     * @param user User transporting the packages
     */
    @Override
    public void notifyCrash(User user, GPSCoordinate coordinate) {
        logger.trace("NotifyCarCrashBean.notifyCrash");
        logger.debug(user.toString());
        for (Mission mission : user.getTransportedMissions()) {
            notifyUser.notifyUser(mission.getOwner(), buildMessage(user.getName()));
            findDriver.findNewDriver(user, mission, coordinate);
        }
    }

    static String buildMessage(String username) {
        return String.format("%s had an accident while transporting your package !", username);
    }

}
