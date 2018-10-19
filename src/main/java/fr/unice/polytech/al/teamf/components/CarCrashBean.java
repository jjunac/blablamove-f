package fr.unice.polytech.al.teamf.components;

import fr.unice.polytech.al.teamf.CarCrash;
import fr.unice.polytech.al.teamf.DriverFinder;
import fr.unice.polytech.al.teamf.NotifyUser;
import fr.unice.polytech.al.teamf.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CarCrashBean implements CarCrash {

    @Autowired
    NotifyUser notifyUser;

    @Autowired
    DriverFinder driverFinder;

    /**
     *
     * @param user User transporting the packages
     */
    @Override
    public void notifyCrash(User user) {
        for (User userToNotify : user.getUsersOwningPackages()){
            notifyUser.notifyUser(userToNotify, buildMessage(user.getName()));
            driverFinder.findNewDriver(user);
        }
    }

    static String buildMessage(String username) {
        return String.format("%s had an accident while transporting your package !", username);
    }

}
