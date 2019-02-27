package fr.unice.polytech.al.teamf.notifier;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.unice.polytech.al.teamf.entities.Answer;
import fr.unice.polytech.al.teamf.entities.Notification;
import fr.unice.polytech.al.teamf.entities.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.io.StringWriter;

@Slf4j
public class Notifier {
    private static final Notifier instance = new Notifier();
    
    private Notifier() {
    }
    
    public static Notifier getInstance() {
        return instance;
    }
    
    public void notifyUser(User user, String message, RabbitTemplate rabbitTemplate) {
        sendNotification(user, new Notification(user, message, null), rabbitTemplate);
    }
    
    public void notifyUserWithAnswer(User user, String message, Answer answer, RabbitTemplate rabbitTemplate) {
        sendNotification(user, new Notification(user, message, answer), rabbitTemplate);
    }
    
    public void sendNotification(User user, Notification notification, RabbitTemplate rabbitTemplate) {
        log.info("Sending notification to " + user.getName());
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode root = objectMapper.createObjectNode().put("username", user.getName());
        root.set("message", objectMapper.valueToTree(notification));
        log.debug(root.toString());
        rabbitTemplate.convertAndSend("notifications", root.toString());
    }
}
