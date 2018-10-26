package fr.unice.polytech.al.teamf.components;

import fr.unice.polytech.al.teamf.ComputePoints;
import fr.unice.polytech.al.teamf.entities.Mission;
import fr.unice.polytech.al.teamf.entities.User;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Component
public class AccountingBean implements ComputePoints {

    @Override
    public int computePoints(User user, Mission mission) {
        int newNbPoints = mission.getRetribution();
        modifyPointsOfUser(user, newNbPoints);
        return mission.getRetribution();
    }

    private void modifyPointsOfUser(User user, int nbPoints) {
        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder
                .fromHttpUrl(String.format("http://localhost:5000/users/%s", user.getName()))
                .queryParam("points", nbPoints);
        try {
            ClientHttpResponse queryResponse = new RestTemplate().execute(uriComponentsBuilder.toUriString(),
                    HttpMethod.PUT,
                    null,
                    clientHttpResponse -> clientHttpResponse);
            if (queryResponse.getStatusCode().is2xxSuccessful()) {
                user.setPoints(nbPoints);
            }
        } catch (ResourceAccessException | HttpClientErrorException e) {
            System.out.println("Impossible to reach accounting server.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
