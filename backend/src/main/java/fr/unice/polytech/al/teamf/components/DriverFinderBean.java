package fr.unice.polytech.al.teamf.components;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.unice.polytech.al.teamf.*;
import fr.unice.polytech.al.teamf.entities.*;
import fr.unice.polytech.al.teamf.repositories.MissionRepository;
import fr.unice.polytech.al.teamf.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

@Slf4j
@Component
public class DriverFinderBean implements FindDriver, AnswerMission {

    RabbitTemplate rabbitTemplate;

    @Autowired
    NotifyUser notifyUser;
    @Autowired
    UserRepository userRepository;
    @Autowired
    MissionRepository missionRepository;
    @Autowired
    FindPackageHost findPackageHost;

    private User currentDriver;
    private Parcel parcel;
    private GPSCoordinate departure;
    private GPSCoordinate arrival;

    public DriverFinderBean(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void findNewDriver(User currentDriver, Parcel parcel, GPSCoordinate departure, GPSCoordinate arrival) {
        log.trace("FindDriverBean.findNewDriver");
        this.currentDriver = currentDriver;
        this.parcel = parcel;
        this.departure = departure;
        this.arrival = arrival;
        String jsonContent = new ObjectMapper()
                .createObjectNode()
                .put("departureLatitude", departure.getLatitude())
                .put("departureLongitude", departure.getLongitude())
                .put("arrivalLatitude", arrival.getLatitude())
                .put("arrivalLongitude", arrival.getLongitude())
                .toString();
        rabbitTemplate.convertAndSend("route-finder", jsonContent);
    }


    @RabbitListener(queues = "routefinding-receiving")
    public void listenRouteFinding(String message){
        log.info("route-finding message : " + message);
        try {
            String username = new ObjectMapper().readTree(message).get("driverName").asText();
            if (Stream.of(username, currentDriver, parcel, departure, arrival).allMatch(Objects::nonNull)) {
                log.info(username + " answers to pending mission");
                User newDriver = userRepository.findByName(username).get(0);
                Mission newMission = new Mission(newDriver, parcel.getOwner(), departure, arrival, parcel);
                missionRepository.save(newMission);
                newDriver.addTransportedMission(newMission);
                parcel.setMission(newMission);

                Map<String, Serializable> parameters = new HashMap<>();
                parameters.put("missionId", newMission.getId());
                parameters.put("username", newDriver.getName());
                notifyUser.notifyUserWithAnswer(newDriver, buildNewDriverMessage(currentDriver.getName(), parcel.getOwner().getName()),
                        new Answer("/package", "answerToPendingMission", parameters));
            }

            findPackageHost.findHost(parcel);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }


    @Override
    public boolean answerToPendingMission(Mission mission, User newDriver, boolean answer) {
        if (answer) {
            notifyUser.notifyUser(mission.getOwner(), buildOwnerMessage(newDriver.getName()));
            notifyUser.notifyUser(mission.getParcel().getKeeper(), buildCurrentDriverMessage(newDriver.getName(), mission.getOwner().getName()));
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
