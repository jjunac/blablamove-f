package fr.unice.polytech.al.teamf.components;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.unice.polytech.al.teamf.FindDriver;
import fr.unice.polytech.al.teamf.NotifyUser;
import fr.unice.polytech.al.teamf.entities.GPSCoordinate;
import fr.unice.polytech.al.teamf.entities.Mission;
import fr.unice.polytech.al.teamf.entities.User;
import fr.unice.polytech.al.teamf.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.JsonParser;
import org.springframework.boot.json.JsonParserFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class FindDriverBean implements FindDriver {
    
    private final Logger logger = LoggerFactory.getLogger(FindDriverBean.class);
    String route_finder_url = "http://route_finder:5000";
    
    @Autowired
    NotifyUser notifyUser;
    @Autowired
    UserRepository userRepository;
    
    @Override
    public User findNewDriver(User currentDriver, Mission mission, GPSCoordinate coordinate) {
        logger.info("FindDriverBean.findNewDriver");
        
        String userName = findUserForRoute(mission);
        if (userName != null) {
            User newDriver = userRepository.findByName(userName).get(0);
            notifyUser.notifyUser(mission.getOwner(), buildOwnerMessage(newDriver.getName()));
            notifyUser.notifyUser(currentDriver, buildCurrentDriverMessage(newDriver.getName(), mission.getOwner().getName()));
            notifyUser.notifyUser(newDriver, buildNewDriverMessage(currentDriver.getName(), mission.getOwner().getName()));
            return newDriver;
        }
        //TODO do something when cannot contact external service
        return null;
    }
    
    private String findUserForRoute(Mission mission) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            String url = String.format("%s/find_driver", route_finder_url);
            
            HashMap<String, Double> params = new HashMap<>();
            params.put("start_lat", mission.getDeparture().getLatitude());
            params.put("start_long", mission.getDeparture().getLongitude());
            params.put("end_lat", mission.getArrival().getLatitude());
            params.put("end_long", mission.getArrival().getLongitude());
            
            
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class, params);
            if (response != null && response.getStatusCode().is2xxSuccessful()) {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode root = mapper.readTree(response.getBody());
                JsonNode name = root.path("name");
                logger.debug(String.format("received: %s, from find_route", root));
                return name.asText();
            }
        } catch (ResourceAccessException | HttpClientErrorException e) {
            logger.error("Impossible to reach server.");
            logger.error(e.getMessage());
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return null;
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
