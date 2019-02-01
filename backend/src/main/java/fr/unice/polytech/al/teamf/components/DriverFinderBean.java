package fr.unice.polytech.al.teamf.components;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.unice.polytech.al.teamf.AnswerMission;
import fr.unice.polytech.al.teamf.FindDriver;
import fr.unice.polytech.al.teamf.FindPackageHost;
import fr.unice.polytech.al.teamf.entities.*;
import fr.unice.polytech.al.teamf.notifier.Notifier;
import fr.unice.polytech.al.teamf.repositories.MissionRepository;
import fr.unice.polytech.al.teamf.repositories.UserRepository;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@Transactional
public class DriverFinderBean implements FindDriver, AnswerMission {
    RabbitTemplate rabbitTemplate;
    @Getter
    @Setter
    public String routeFinderUrl = "http://route_finder:5000";
    
    private Notifier notifier = Notifier.getInstance();
    
    @Autowired
    UserRepository userRepository;
    @Autowired
    MissionRepository missionRepository;
    @Autowired
    FindPackageHost findPackageHost;
    
    public DriverFinderBean(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }
    
    @Override
    public User findNewDriver(User currentDriver, Parcel parcel, GPSCoordinate coordinate, GPSCoordinate arrival) {
        log.trace("FindDriverBean.findNewDriver");
        
        
        String username = findUserForRoute(coordinate, arrival);
        if (username != null) {
            User newDriver = userRepository.findByName(username).get(0);
            Mission newMission = new Mission(newDriver, parcel.getOwner(), coordinate, arrival, parcel);
            newDriver.addTransportedMission(newMission);
            parcel.setMission(newMission);
            newMission = missionRepository.save(newMission);
            Map<String, Serializable> parameters = new HashMap<>();
            parameters.put("missionId", newMission.getId());
            parameters.put("username", newDriver.getName());
            notifier.notifyUserWithAnswer(newDriver, buildNewDriverMessage(currentDriver.getName(), parcel.getOwner().getName()),
                    new Answer("/package", "answerToPendingMission", parameters),rabbitTemplate);
            return newDriver;
        }
        
        findPackageHost.findHost(parcel);
        
        return null;
    }
    
    private String findUserForRoute(GPSCoordinate departure, GPSCoordinate arrival) {
        String jsonContent = new ObjectMapper()
                .createObjectNode()
                .put("departureLatitude", departure.getLatitude())
                .put("departureLongitude", departure.getLongitude())
                .put("arrivalLatitude", arrival.getLatitude())
                .put("arrivalLongitude", arrival.getLongitude())
                .toString();
        rabbitTemplate.convertAndSend("route-finder-exchange", "routefinder.finduser", jsonContent);
        return "Erick";
    }
    
    @Override
    public boolean answerToPendingMission(Mission mission, User newDriver, boolean answer) {
        if (answer) {
            notifier.notifyUser(mission.getOwner(), buildOwnerMessage(newDriver.getName()), rabbitTemplate);
            notifier.notifyUser(mission.getParcel().getKeeper(), buildCurrentDriverMessage(newDriver.getName(), mission.getOwner().getName()), rabbitTemplate);
        } else {
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
        return String.format("Could you please take %s's package in %s's car ?", ownerName, currentDriverName);
    }
    
}
