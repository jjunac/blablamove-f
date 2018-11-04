package fr.unice.polytech.al.teamf.components;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.unice.polytech.al.teamf.NotifyCarCrash;
import fr.unice.polytech.al.teamf.FindDriver;
import fr.unice.polytech.al.teamf.NotifyUser;
import fr.unice.polytech.al.teamf.entities.GPSCoordinate;
import fr.unice.polytech.al.teamf.entities.Mission;
import fr.unice.polytech.al.teamf.entities.Parcel;
import fr.unice.polytech.al.teamf.entities.User;
import fr.unice.polytech.al.teamf.exceptions.UnknownUserException;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

@Slf4j
@Component
public class NotifyCarCrashBean implements NotifyCarCrash {

    private final Logger logger = LoggerFactory.getLogger(NotifyCarCrashBean.class);

    String insurance_url = "http://insurance:5000";

    @Autowired
    NotifyUser notifyUser;

    @Autowired
    FindDriver findDriver;

    /**
     * @param user User transporting the packages
     */
    @Override
    public void notifyCrash(User user, GPSCoordinate coordinate) {
        logger.trace("NotifyCarCrashBean.notifyCrash");
        logger.debug(user.toString());
        for (Mission mission : user.getTransportedMissions()) {
            notifyUser.notifyUser(mission.getOwner(), buildMessage(user.getName()));
            findDriver.findNewDriver(user, mission, coordinate);
            log.debug(""+contactInsurance(user));
        }
    }

    boolean contactInsurance(User user) {
        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder
                .fromHttpUrl(String.format("%s/insurance/%s", insurance_url, user.getName()));
        try {
            ClientHttpResponse queryResponse = new RestTemplate().execute(uriComponentsBuilder.toUriString(),
                    HttpMethod.GET,
                    null,
                    clientHttpResponse -> clientHttpResponse);
            if (queryResponse.getStatusCode().is2xxSuccessful()) {
                return new ObjectMapper()
                        .readTree(new BufferedReader(
                                new InputStreamReader(queryResponse.getBody()))
                                .lines()
                                .collect(Collectors.joining("")))
                        .get("insuranceInvolvement")
                        .asBoolean();
            }
        } catch (ResourceAccessException | HttpClientErrorException e) {
            log.error("Impossible to reach server.");
            log.error(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false; // handle this properly
    }

    static String buildMessage(String username) {
        return String.format("%s had an accident while transporting your package !", username);
    }

}
