package fr.unice.polytech.al.teamf;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.BufferedReader;
import java.io.InputStreamReader;

@Slf4j
@Component
public class Receiver {

    private final RabbitTemplate rabbitTemplate;

    private String point_pricing_url = "http://point_pricing:5000";

    public Receiver(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    /**
     * user:nbPoints
     * @param message
     */
    public void receiveMessage(String message) throws UnknownUserException {
        String[] split = message.split(":");
        String user = split[0];
        int nbPoints = Integer.parseInt(split[1]);
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
                String nbPointsAfterModification = new BufferedReader(new InputStreamReader(queryResponse.getBody())).readLine();
                rabbitTemplate.convertAndSend(Application.topicExchangeName, "foo.bar.baz", nbPointsAfterModification);
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
