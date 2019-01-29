package fr.unice.polytech.al.teamf.message_listeners;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Class invoked when a message is received in a messaging queue,
 * for example when an external service notifies an executed action.
 */
@Slf4j
@Component
public class RouteFindingMessageReceiver {

    public void receiveMessage(String message){
        log.info(message);
    }

}
