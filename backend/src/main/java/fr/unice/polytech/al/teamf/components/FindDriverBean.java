package fr.unice.polytech.al.teamf.components;

import fr.unice.polytech.al.teamf.FindDriver;
import fr.unice.polytech.al.teamf.FindPackageHost;
import fr.unice.polytech.al.teamf.NotifyUser;
import fr.unice.polytech.al.teamf.entities.Answer;
import fr.unice.polytech.al.teamf.entities.GPSCoordinate;
import fr.unice.polytech.al.teamf.entities.Mission;
import fr.unice.polytech.al.teamf.entities.User;
import fr.unice.polytech.al.teamf.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Component
public class FindDriverBean implements FindDriver {

    private final Logger logger = LoggerFactory.getLogger(FindDriverBean.class);

    @Autowired
    NotifyUser notifyUser;
    @Autowired
    UserRepository userRepository;
    @Autowired
    FindPackageHost findPackageHost;

    @Override
    public User findNewDriver(User currentDriver, Mission mission, GPSCoordinate coordinate) {
        logger.info("FindDriverBean.findNewDriver");


        // TODO call external webservice
        // Mocking new user
        User newDriver = userRepository.findByName("Erick").get(0);

        Mission newMission = new Mission(newDriver, mission.getOwner(), coordinate, mission.getArrival(), mission.getParcel());
        newDriver.addTransportedMission(newMission);

        Map<String, Serializable> parameters = new HashMap<>();
        parameters.put("missionId", newMission.getId());
        parameters.put("username", newDriver.getName());
        notifyUser.notifyUserWithAnswer(newDriver, buildNewDriverMessage(currentDriver.getName(), mission.getOwner().getName()),
                new Answer("/package", "answerToPendingMission", parameters));
        return newDriver;
    }

    @Override
    public void answerToPendingMission(Mission mission, User newDriver, boolean answer) {
        if(answer) {
            notifyUser.notifyUser(mission.getOwner(), buildOwnerMessage(newDriver.getName()));
            notifyUser.notifyUser(mission.getParcel().getKeeper(), buildCurrentDriverMessage(newDriver.getName(), mission.getOwner().getName()));
        } else {
            // TODO Pass the localisation
            findPackageHost.findHost(mission.getParcel());
        }
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
