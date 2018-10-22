package fr.unice.polytech.al.teamf.components;

import fr.unice.polytech.al.teamf.NotifyUser;
import fr.unice.polytech.al.teamf.entities.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.PrintStream;

@Component
public class UserNotifierBean implements NotifyUser {

    private final Logger logger = LoggerFactory.getLogger(UserNotifierBean.class);
    PrintStream printStream = System.out;

    @Override
    public void notifyUser(User user, String message) {
        String msg = String.format("Send message to %s: %s", user.getName(), message);
        printStream.println(msg);
        logger.info(msg);
    }

}
