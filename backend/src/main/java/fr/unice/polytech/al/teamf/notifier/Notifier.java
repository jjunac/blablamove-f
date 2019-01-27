package fr.unice.polytech.al.teamf.notifier;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.unice.polytech.al.teamf.entities.User;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

public class Notifier {
    private static final Notifier instance = new Notifier();
    private static final RabbitTemplate rabbitTemplate = new RabbitTemplate();

    private Notifier(){}

    public static Notifier getInstance(){
        return instance;
    }

    public void sendNotification(User receiver, String notificationContent, boolean answerNeeded){
        String jsonContent = new ObjectMapper()
                .createObjectNode()
                .put("content", notificationContent)
                .put("receiver", receiver.getName())
                .toString();
        if (answerNeeded){
            rabbitTemplate.convertAndSend("notifications-exchange", "notifications.with_answer", jsonContent);
        } else {
            rabbitTemplate.convertAndSend("notifications-exchange", "notifications.simple", jsonContent);
        }
    }
}
