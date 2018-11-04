package fr.unice.polytech.al.teamf.components;

import fr.unice.polytech.al.teamf.ComputePoints;
import fr.unice.polytech.al.teamf.entities.Mission;
import fr.unice.polytech.al.teamf.entities.User;
import fr.unice.polytech.al.teamf.exceptions.UnknownUserException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.BufferedReader;
import java.io.InputStreamReader;


@Component
public class AccountingBean implements ComputePoints {
    String point_pricing_url="http://point_pricing:5001";
    
    @Override
    public int computePoints(Mission mission) throws UnknownUserException {
        int newNbPoints = mission.computeRetribution();
        return modifyPointsOfUser(mission.getTransporter(), newNbPoints);
    }
    
    private int modifyPointsOfUser(User user, int nbPoints) throws UnknownUserException {
        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder
                .fromHttpUrl(String.format("%s/users/%s", point_pricing_url, user.getName()))
                .queryParam("points", nbPoints);
        try {
            ClientHttpResponse queryResponse = new RestTemplate().execute(uriComponentsBuilder.toUriString(),
                    HttpMethod.PUT,
                    null,
                    clientHttpResponse -> clientHttpResponse);
            if (queryResponse.getStatusCode().is2xxSuccessful()) {
                return Integer.parseInt(new BufferedReader(new InputStreamReader(queryResponse.getBody())).readLine());
            } else if (queryResponse.getStatusCode().is4xxClientError()) {
                throw new UnknownUserException(user);
            }
        } catch (ResourceAccessException | HttpClientErrorException e) {
            System.out.println("Impossible to reach accounting server.");
            System.out.println(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0; // handle this properly
    }
    
    
}
