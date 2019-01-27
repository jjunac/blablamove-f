package fr.unice.polytech.al.teamf.usernotifier;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
            JsonNode node = new ObjectMapper().readTree(in);
            String username = node.get("username").asText();
            String message = node.get("message").asText();
            notifyUser.notifyUser(username, message);
        } catch (Exception exception) {
            log.error(exception.getMessage(), exception);
        }
    }
}
