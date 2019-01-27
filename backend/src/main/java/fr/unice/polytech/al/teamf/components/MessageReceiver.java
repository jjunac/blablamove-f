package fr.unice.polytech.al.teamf.components;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Class invoked when a message is received in a messaging queue,
 * for example when an external service notifies an executed action.
 */
@Slf4j
@Component
public class MessageReceiver {

    public void receiveMessage(String message){
        log.info(message);
    }

}
