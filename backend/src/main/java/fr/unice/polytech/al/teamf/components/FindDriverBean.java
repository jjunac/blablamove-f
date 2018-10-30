package fr.unice.polytech.al.teamf.components;

import fr.unice.polytech.al.teamf.FindDriver;
import fr.unice.polytech.al.teamf.NotifyUser;
import fr.unice.polytech.al.teamf.entities.Parcel;
import fr.unice.polytech.al.teamf.entities.User;
import fr.unice.polytech.al.teamf.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FindDriverBean implements FindDriver {

    private final Logger logger = LoggerFactory.getLogger(FindDriverBean.class);

    @Autowired
    NotifyUser notifyUser;
    @Autowired
    UserRepository userRepository;

    @Override
    public User findNewDriver(User currentDriver, Parcel parcel) {
        logger.info("FindDriverBean.findNewDriver");
        // Mocking new user
        User newDriver = userRepository.findByName("Erick").get(0);
        notifyUser.notifyUser(parcel.getOwner(), buildOwnerMessage(newDriver.getName()));
        notifyUser.notifyUser(currentDriver, buildCurrentDriverMessage(newDriver.getName(), parcel.getOwner().getName()));
        notifyUser.notifyUser(newDriver, buildNewDriverMessage(parcel.getOwner().getName(), currentDriver.getName()));
        return newDriver;

    }

    static String buildOwnerMessage(String newDriverName) {
        return String.format("%s is taking your package !", newDriverName);
    }

    static String buildCurrentDriverMessage(String newDriverName, String ownerName) {
        return String.format("%s is taking %s's package !", newDriverName, ownerName);
    }

    static String buildNewDriverMessage(String currentDriverName, String ownerName) {
        return String.format("You will take %s's package in %s's car !", ownerName, currentDriverName);
    }

}
