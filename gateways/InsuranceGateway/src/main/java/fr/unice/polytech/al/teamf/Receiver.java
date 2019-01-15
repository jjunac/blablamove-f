package fr.unice.polytech.al.teamf;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.CommandLineRunner;
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
public class Receiver implements CommandLineRunner {

    private final RabbitTemplate rabbitTemplate;

    private String insurance_url = "http://insurance:5000";

    public Receiver(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void run(String... args) throws Exception {
        Thread.currentThread().join();
    }

    /**
     * Example of expected message : {"user": "Erick"}
     * Example of returned message : {"insurance_involvement": True}
     */
    public void receiveMessage(String message) throws IOException {
        JsonNode node = new ObjectMapper().readTree(message);
        String user = node.get("user").asText();
        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder
                .fromHttpUrl(String.format("%s/insurance/%s", insurance_url, user));
        try {
            ClientHttpResponse queryResponse = new RestTemplate().execute(uriComponentsBuilder.toUriString(),
                    HttpMethod.GET,
                    null,
                    clientHttpResponse -> clientHttpResponse);
            log.debug("contacting insurance with user " + user);
            if (queryResponse.getStatusCode().is2xxSuccessful()) {
                boolean result = new ObjectMapper()
                        .readTree(queryResponse.getBody())
                        .get("insuranceInvolvement").asBoolean();
                String jsonContent = new ObjectMapper()
                        .createObjectNode()
                        .put("insurance_involvement", result)
                        .toString();
                rabbitTemplate.convertAndSend(Application.topicExchangeName, "external.insurance", jsonContent);
            }
        } catch (ResourceAccessException | HttpClientErrorException e) {
            log.error("Impossible to reach insurance service.");
            log.error(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
