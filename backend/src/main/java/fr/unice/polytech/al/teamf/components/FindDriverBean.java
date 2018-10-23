package fr.unice.polytech.al.teamf.components;

import fr.unice.polytech.al.teamf.FindDriver;
import fr.unice.polytech.al.teamf.NotifyUser;
import fr.unice.polytech.al.teamf.entities.Parcel;
import fr.unice.polytech.al.teamf.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FindDriverBean implements FindDriver {

    @Autowired
    NotifyUser notifyUser;

    @Override
    public User findNewDriver(User currentDriver, Parcel parcel) {
        // Mocking new user
        User newDriver = new User("Erick");
        notifyUser.notifyUser(parcel.getOwner(), buildOwnerMessage(newDriver.getName()));
        notifyUser.notifyUser(currentDriver, buildDriverMessage(newDriver.getName(), parcel.getOwner().getName()));
        return newDriver;

    }

    static String buildOwnerMessage(String newDriverName) {
        return String.format("%s is taking your package !", newDriverName);
    }

    static String buildDriverMessage(String newDriverName, String ownerName) {
        return String.format("%s is taking %s's package !", newDriverName, ownerName);
    }

}
