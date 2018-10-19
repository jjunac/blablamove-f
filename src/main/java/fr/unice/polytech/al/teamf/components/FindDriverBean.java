package fr.unice.polytech.al.teamf.components;

import fr.unice.polytech.al.teamf.FindDriver;
import fr.unice.polytech.al.teamf.NotifyUser;
import fr.unice.polytech.al.teamf.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FindDriverBean implements FindDriver {

    @Autowired
    NotifyUser notifyUser;

    @Override
    public User findNewDriver(User user) {
        // Mocking new user
        User newDriver = new User("Erick");
        notifyUser.notifyUser(user, buildMessage(newDriver.getName()));
        return newDriver;

    }

    static String buildMessage(String username) {
        return String.format("%s is taking your package !", username);
    }

}
