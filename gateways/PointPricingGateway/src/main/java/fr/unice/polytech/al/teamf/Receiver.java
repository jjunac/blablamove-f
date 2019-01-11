package fr.unice.polytech.al.teamf;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Slf4j
@Component
public class Receiver {

    private final RabbitTemplate rabbitTemplate;

    private String point_pricing_url = "http://localhost:5000";

    public Receiver(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    /**
     * Example of expected message : {"user": "Erick", "points": 10}
     * Example of returned message : {"points": 60}
     */
    public void receiveMessage(String message) throws UnknownUserException, IOException {
        JsonNode node = new ObjectMapper().readTree(message);
        String user = node.get("user").asText();
        int nbPoints = node.get("points").asInt();
        log.info(String.format("Add %d points to %s", nbPoints, user));
        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder
                .fromHttpUrl(String.format("%s/users/%s", point_pricing_url, user))
                .queryParam("points", nbPoints);
        try {
            ClientHttpResponse queryResponse = new RestTemplate().execute(uriComponentsBuilder.toUriString(),
                    HttpMethod.PUT,
                    null,
                    clientHttpResponse -> clientHttpResponse);
            if (queryResponse.getStatusCode().is2xxSuccessful()) {
                int nbPointsAfterModification = new ObjectMapper().readTree(queryResponse.getBody()).get("points").asInt();
                String jsonContent = new ObjectMapper()
                        .createObjectNode()
                        .put("points", nbPointsAfterModification)
                        .toString();
                rabbitTemplate.convertAndSend(Application.topicExchangeName, "pointpricing.points", jsonContent);
            } else if (queryResponse.getStatusCode().is4xxClientError()) {
                throw new UnknownUserException(user);
            }
        } catch (ResourceAccessException | HttpClientErrorException e) {
            log.error("Impossible to reach accounting server.");
            log.error(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
