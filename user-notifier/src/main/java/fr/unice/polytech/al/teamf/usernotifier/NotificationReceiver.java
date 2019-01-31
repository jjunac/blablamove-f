package fr.unice.polytech.al.teamf.usernotifier;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.unice.polytech.al.teamf.usernotifier.entities.Answer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Slf4j
@Component
@RabbitListener(queues = "notifications")
public class NotificationReceiver {
    private final NotifyUser notifyUser;
    
    @Autowired
    public NotificationReceiver(NotifyUser notifyUser) {
        this.notifyUser = notifyUser;
    }
    
    
    @RabbitHandler
    public void receive(byte[] in) {
        try {
            log.info(new String(in));
            JsonNode node = new ObjectMapper().readTree(in);
            if (node.has("username") && node.has("message")) {
                String username = node.get("username").asText();
                JsonNode message = node.get("message");
                String textMessage = message.get("message").asText();
                if (message.has("answer") && !message.get("answer").isNull()) {
                    log.info("Message for " + username + " has answer");
                    log.debug(message.get("answer").toString());
                    Answer answer = new ObjectMapper().treeToValue(message.get("answer"), Answer.class);
                    notifyUser.notifyUserWithAnswer(username, textMessage, answer);
                } else {
                    notifyUser.notifyUser(username, textMessage);
                }
            }
        } catch (Exception exception) {
            log.error(exception.getMessage(), exception);
        }
    }
}
