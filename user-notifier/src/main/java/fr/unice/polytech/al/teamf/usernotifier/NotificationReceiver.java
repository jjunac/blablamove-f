package fr.unice.polytech.al.teamf.usernotifier;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
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
        log.info("teeeeeeeeeeeeeeeest");
        this.notifyUser = notifyUser;
    }


    @RabbitHandler
    public void receive(byte[] in) {
        String stringified = new String(in);
        log.info(" [x] Received '" + stringified + "'");
        try {
            notifyUser.notifyUser("lol", "lol");
        } catch (Exception exception) {
            log.error(exception.toString());
        }
    }
}
