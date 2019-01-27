package fr.unice.polytech.al.teamf.components;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.unice.polytech.al.teamf.*;
import fr.unice.polytech.al.teamf.entities.*;
import fr.unice.polytech.al.teamf.notifier.Notifier;
import fr.unice.polytech.al.teamf.repositories.MissionRepository;
import fr.unice.polytech.al.teamf.repositories.UserRepository;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.Map;

import java.io.Serializable;

@Slf4j
@Component
public class DriverFinderBean implements FindDriver, AnswerMission {
    @Getter
    @Setter
    public String routeFinderUrl = "http://route_finder:5000";

    private Notifier notifier = Notifier.getInstance();

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


        String username = findUserForRoute(coordinate, arrival);
        if (username != null) {
            User newDriver = userRepository.findByName(username).get(0);
            Mission newMission = new Mission(newDriver, parcel.getOwner(), coordinate, arrival, parcel);
            missionRepository.save(newMission);
            newDriver.addTransportedMission(newMission);
            parcel.setMission(newMission);

            Map<String, Serializable> parameters = new HashMap<>();
            parameters.put("missionId", newMission.getId());
            parameters.put("username", newDriver.getName());
            notifier.sendNotification(newDriver, buildNewDriverMessage(currentDriver.getName(), parcel.getOwner().getName()), true);
//            notifyUser.notifyUserWithAnswer(newDriver, buildNewDriverMessage(currentDriver.getName(), parcel.getOwner().getName()),
//                    new Answer("/package", "answerToPendingMission", parameters));
            return newDriver;
        }

        findPackageHost.findHost(parcel);

        return null;
    }

    private String findUserForRoute(GPSCoordinate departure, GPSCoordinate arrival) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            String url = String.format("%s/find_driver", routeFinderUrl);
            HashMap<String, Double> params = new HashMap<>();
            UriComponentsBuilder builder = UriComponentsBuilder
                    .fromUriString(url)
                    .queryParam("start_lat", departure.getLatitude())
                    .queryParam("start_long", departure.getLongitude())
                    .queryParam("end_lat", arrival.getLatitude())
                    .queryParam("end_long", arrival.getLongitude());

            log.debug("trying to get " + builder.toUriString());
            ResponseEntity<String> response = restTemplate.getForEntity(builder.toUriString(), String.class, params);
            if (response != null && response.getStatusCode().is2xxSuccessful()) {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode root = mapper.readTree(response.getBody());
                JsonNode name = root.get("drivers").get(0).get("name");
                log.debug(String.format("received: %s, from find_route name=%s", root, name));
                return name.asText();
            }
        } catch (ResourceAccessException | HttpClientErrorException e) {
            log.error("Impossible to reach server.");
            log.error(e.getMessage());
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return "Erick";
    }

    @Override
    public boolean answerToPendingMission(Mission mission, User newDriver, boolean answer) {
        if (answer) {
//            notifyUser.notifyUser(mission.getOwner(), buildOwnerMessage(newDriver.getName()));
//            notifyUser.notifyUser(mission.getParcel().getKeeper(), buildCurrentDriverMessage(newDriver.getName(), mission.getOwner().getName()));
            notifier.sendNotification(mission.getOwner(), buildOwnerMessage(newDriver.getName()), false);
            notifier.sendNotification(mission.getParcel().getKeeper(), buildCurrentDriverMessage(newDriver.getName(), mission.getOwner().getName()), false);
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
