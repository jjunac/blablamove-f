package fr.unice.polytech.al.teamf.components;

import fr.unice.polytech.al.teamf.FindDriver;
import fr.unice.polytech.al.teamf.FindPackageHost;
import fr.unice.polytech.al.teamf.NotifyUser;
import fr.unice.polytech.al.teamf.entities.*;
import fr.unice.polytech.al.teamf.repositories.MissionRepository;
import fr.unice.polytech.al.teamf.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class FindDriverBean implements FindDriver {

    @Autowired
    NotifyUser notifyUser;
    @Autowired
    UserRepository userRepository;
    @Autowired
    MissionRepository missionRepository;
    @Autowired
    FindPackageHost findPackageHost;

    @Override
    public User findNewDriver(User currentDriver, Parcel parcel, GPSCoordinate coordinate, GPSCoordinate arrival) {
        log.trace("FindDriverBean.findNewDriver");


        // TODO call external webservice
        // Mocking new user
        User newDriver = userRepository.findByName("Erick").get(0);

        Mission newMission = new Mission(newDriver, parcel.getOwner(), coordinate, arrival, parcel);
        missionRepository.save(newMission);
        newDriver.addTransportedMission(newMission);
        parcel.setMission(newMission);

        Map<String, Serializable> parameters = new HashMap<>();
        parameters.put("missionId", newMission.getId());
        parameters.put("username", newDriver.getName());
        notifyUser.notifyUserWithAnswer(newDriver, buildNewDriverMessage(currentDriver.getName(), parcel.getOwner().getName()),
                new Answer("/package", "answerToPendingMission", parameters));
        return newDriver;
    }

    @Override
    public boolean answerToPendingMission(Mission mission, User newDriver, boolean answer) {
        if(answer) {
            notifyUser.notifyUser(mission.getOwner(), buildOwnerMessage(newDriver.getName()));
            //log.debug(mission.toString());
            //log.debug(mission.getParcel().toString());
            notifyUser.notifyUser(mission.getParcel().getKeeper(), buildCurrentDriverMessage(newDriver.getName(), mission.getOwner().getName()));
        } else {
            // TODO Pass the localisation
            findPackageHost.findHost(mission.getParcel());
        }
        return true;
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
