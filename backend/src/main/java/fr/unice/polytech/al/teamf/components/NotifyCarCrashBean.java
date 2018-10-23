package fr.unice.polytech.al.teamf.components;

import fr.unice.polytech.al.teamf.NotifyCarCrash;
import fr.unice.polytech.al.teamf.FindDriver;
import fr.unice.polytech.al.teamf.NotifyUser;
import fr.unice.polytech.al.teamf.entities.Parcel;
import fr.unice.polytech.al.teamf.entities.User;
import fr.unice.polytech.al.teamf.webservices.IncidentServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class NotifyCarCrashBean implements NotifyCarCrash {

    private final Logger logger = LoggerFactory.getLogger(IncidentServiceImpl.class);

    @Autowired
    NotifyUser notifyUser;

    @Autowired
    FindDriver findDriver;

    /**
     *
     * @param user User transporting the packages
     */
    @Override
    public void notifyCrash(User user) {
        logger.trace("NotifyCarCrashBean.notifyCrash");
        for (Parcel parcel : user.getTransportedPackages()){
            notifyUser.notifyUser(parcel.getOwner(), buildMessage(user.getName()));
            findDriver.findNewDriver(user, parcel);
        }
    }

    static String buildMessage(String username) {
        return String.format("%s had an accident while transporting your package !", username);
    }

}
