package fr.unice.polytech.al.teamf.components;

import fr.unice.polytech.al.teamf.NotifyCarCrash;
import fr.unice.polytech.al.teamf.FindDriver;
import fr.unice.polytech.al.teamf.NotifyUser;
import fr.unice.polytech.al.teamf.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class NotifyCarCrashBean implements NotifyCarCrash {

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
        for (User userToNotify : user.getUsersOwningPackages()){
            notifyUser.notifyUser(userToNotify, buildMessage(user.getName()));
            findDriver.findNewDriver(user);
        }
    }

    static String buildMessage(String username) {
        return String.format("%s had an accident while transporting your package !", username);
    }

}
