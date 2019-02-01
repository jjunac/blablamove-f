package fr.unice.polytech.al.teamf;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.CommandLineRunner;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.HashMap;

@Slf4j
@Component
public class Receiver implements CommandLineRunner {

    private final RabbitTemplate rabbitTemplate;

    public String routeFinderUrl = "http://route_finder:5000";

    public Receiver(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void run(String... args) throws Exception {
        Thread.currentThread().join();
    }

    /**
     * Example of expected message : {"departureLatitude": 10.0, "departureLongitude": 15.0, "arrivalLatitude": 20.5, "arrivalLongitude": 42.89}
     * Example of returned message : {"driver": "Erick"}
     */
    public void receiveMessage(String message) throws IOException {
        JsonNode node = new ObjectMapper().readTree(message);
        log.info("Finding new driver");
        try {
            RestTemplate restTemplate = new RestTemplate();
            String url = String.format("%s/find_driver", routeFinderUrl);
            HashMap <String, Double> params = new HashMap<>();
            UriComponentsBuilder builder = UriComponentsBuilder
                    .fromUriString(url)
                    .queryParam("start_lat", node.get("departureLatitude").asDouble())
                    .queryParam("start_long", node.get("departureLongitude").asDouble())
                    .queryParam("end_lat", node.get("arrivalLatitude").asDouble())
                    .queryParam("end_long", node.get("arrivalLatitude").asDouble());

            log.debug("trying to get " + builder.toUriString());
            ResponseEntity <String> response = restTemplate.getForEntity(builder.toUriString(), String.class, params);
            if (response != null && response.getStatusCode().is2xxSuccessful()) {

                ObjectMapper mapper = new ObjectMapper();
                JsonNode root = mapper.readTree(response.getBody());
                JsonNode name = root.get("drivers").get(0).get("name");
                log.debug(String.format("received: %s, from find_route name=%s", root, name));
                String jsonContent = new ObjectMapper()
                        .createObjectNode()
                        .put("driverName", name.toString())
                        .toString();
                rabbitTemplate.convertAndSend("routefinding-receiving-exchange", "external.routefinder", jsonContent);
            }
        } catch (ResourceAccessException | HttpClientErrorException e) {
            log.error("Impossible to reach server.");
            log.error(e.getMessage());
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

}
