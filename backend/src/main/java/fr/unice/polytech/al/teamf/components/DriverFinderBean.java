package fr.unice.polytech.al.teamf.components;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.unice.polytech.al.teamf.AnswerMission;
import fr.unice.polytech.al.teamf.FindDriver;
import fr.unice.polytech.al.teamf.FindPackageHost;
import fr.unice.polytech.al.teamf.entities.*;
import fr.unice.polytech.al.teamf.notifier.Notifier;
import fr.unice.polytech.al.teamf.repositories.MissionRepository;
import fr.unice.polytech.al.teamf.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Slf4j
@Transactional
@Component
public class DriverFinderBean implements FindDriver, AnswerMission {
    RabbitTemplate rabbitTemplate;
    
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
    
    private List<Function<String, Void>> functions = new ArrayList<>();
    
    @Override
    public void findNewDriver(User currentDriver, Parcel parcel, GPSCoordinate departure, GPSCoordinate arrival) {
        log.trace("FindDriverBean.findNewDriver");
        
        String jsonContent = new ObjectMapper()
                .createObjectNode()
                .put("departureLatitude", departure.getLatitude())
                .put("departureLongitude", departure.getLongitude())
                .put("arrivalLatitude", arrival.getLatitude())
                .put("arrivalLongitude", arrival.getLongitude())
                .toString();
        rabbitTemplate.convertAndSend("route-finder", jsonContent);
        Message message = rabbitTemplate.receive("routefinding-receiving",2000);
        try {
            String username = new ObjectMapper().readTree(message.getBody()).get("driverName").asText();
            log.info(username + " found");
            User newDriver = userRepository.findByName(username).get(0);
            Mission newMission = new Mission(newDriver, parcel.getOwner(), departure, arrival, parcel);
            newDriver.addTransportedMission(newMission);
            parcel.setMission(newMission);
            newMission = missionRepository.save(newMission);
            Map<String, Serializable> parameters = new HashMap<>();
            log.info("mission " + newMission.getId() + "parcel" + parcel.getId());
            parameters.put("missionId", newMission.getId());
            parameters.put("username", newDriver.getName());
            notifier.notifyUserWithAnswer(newDriver, buildNewDriverMessage(currentDriver.getName(), parcel.getOwner().getName()),
                    new Answer("/package", "answerToPendingMission", parameters), rabbitTemplate);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        
    }
    
//    @Transactional
////    @RabbitListener(queues = "routefinding-receiving")
//    public void listenRouteFinding(String message) {
//        log.trace("FindDriverBean.listenRouteFinding");
//        log.info("route-finding message : " + message);
//        try {
//            String username = new ObjectMapper().readTree(message).get("driverName").asText();
////            Function<String, Void> func = functions.remove(0);
////            func.apply(username);
//
//            //            findPackageHost.findHost(parcel);
//        } catch (IOException e) {
//            log.error(e.getMessage());
//        }
//    }
    
    @Override
    public boolean answerToPendingMission(long missionId, String newDriverName, boolean answer) {
        Mission mission = missionRepository.findById(missionId).get();
        User newDriver = userRepository.findByName(newDriverName).get(0);
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
