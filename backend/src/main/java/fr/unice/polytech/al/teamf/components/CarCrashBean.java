package fr.unice.polytech.al.teamf.components;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.unice.polytech.al.teamf.NotifyCarCrash;
import fr.unice.polytech.al.teamf.FindDriver;
import fr.unice.polytech.al.teamf.entities.GPSCoordinate;
import fr.unice.polytech.al.teamf.entities.Mission;
import fr.unice.polytech.al.teamf.entities.Parcel;
import fr.unice.polytech.al.teamf.entities.User;
import fr.unice.polytech.al.teamf.notifier.Notifier;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@Component
public class CarCrashBean implements NotifyCarCrash {
    
    String insurance_url = "http://insurance:5000";

    private Notifier notifier = Notifier.getInstance();

    @Autowired
    FindDriver findDriver;
    @Autowired
    AccountingBean accountingBean;

    /**
     * @param user User transporting the packages
     */
    @Override
    public void notifyCrash(User user, GPSCoordinate coordinate) {
        log.trace("NotifyCarCrashBean.notifyCrash");
        boolean reachedInsurance = contactInsurance(user);
        log.debug(Boolean.toString(reachedInsurance));
        for (Mission mission : user.getTransportedMissionsWithStatus(Mission.Status.ONGOING)) {
            accountingBean.computePoints(mission);
            notifier.sendNotification(mission.getOwner(), buildMessage(user.getName()), false);
//            notifyUser.notifyUser(mission.getOwner(), buildMessage(user.getName()));
            Parcel parcel = mission.getParcel();
            parcel.setMission(null);
            mission.setParcel(null);
            findDriver.findNewDriver(user, parcel, coordinate, mission.getArrival());
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
            log.debug("contacting insurance with user " + user.getName());
            if (queryResponse.getStatusCode().is2xxSuccessful()) {
                return new ObjectMapper()
                        .readTree(queryResponse.getBody())
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
