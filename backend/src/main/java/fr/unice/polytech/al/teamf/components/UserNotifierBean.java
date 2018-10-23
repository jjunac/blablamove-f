package fr.unice.polytech.al.teamf.components;

import fr.unice.polytech.al.teamf.NotifyUser;
import fr.unice.polytech.al.teamf.PullNotifications;
import fr.unice.polytech.al.teamf.entities.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Component
public class UserNotifierBean implements NotifyUser, PullNotifications {

    private final Logger logger = LoggerFactory.getLogger(UserNotifierBean.class);

    private Map<String, List<String>> userNotifications = new HashMap<>();

    @Override
    public void notifyUser(User user, String message) {
        userNotifications.computeIfAbsent(user.getName(), k -> new LinkedList<>()).add(message);
        logger.info(String.format("Send message to %s: %s", user.getName(), message));
    }

    @Override
    public List<String> pullNotificationForUser(String username) {
        logger.info(String.format("%s is pulling its notifications", username));
        List<String> res = userNotifications.remove(username);
        return res != null ? res : new LinkedList<>();
    }
}
