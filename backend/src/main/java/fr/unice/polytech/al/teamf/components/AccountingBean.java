package fr.unice.polytech.al.teamf.components;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.unice.polytech.al.teamf.ComputePoints;
import fr.unice.polytech.al.teamf.entities.Mission;
import fr.unice.polytech.al.teamf.entities.User;
import fr.unice.polytech.al.teamf.exceptions.UnknownUserException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AccountingBean implements ComputePoints {

    @Value("${point_pricing_address}")
    String point_pricing_url;

    RabbitTemplate rabbitTemplate;

    public AccountingBean(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }


    @Override
    public int computePoints(Mission mission) {
        int newNbPoints = mission.computeRetribution();
        try {
            return modifyPointsOfUser(mission.getTransporter(), newNbPoints);
        } catch (UnknownUserException e) {
            log.error(e.getMessage());
        }
        return 0;
    }
    
    private int modifyPointsOfUser(User user, int nbPoints) throws UnknownUserException {
        String jsonContent = new ObjectMapper()
                .createObjectNode()
                .put("user", user.getName())
                .put("points", nbPoints)
                .toString();
        rabbitTemplate.convertAndSend("point-pricing-exchange", "pointpricing.points", jsonContent);
        return 0; // handle this properly
    }
    
    
}
