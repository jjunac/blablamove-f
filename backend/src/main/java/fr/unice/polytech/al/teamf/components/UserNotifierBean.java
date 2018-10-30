package fr.unice.polytech.al.teamf.components;

import fr.unice.polytech.al.teamf.NotifyUser;
import fr.unice.polytech.al.teamf.PullNotifications;
import fr.unice.polytech.al.teamf.entities.Notification;
import fr.unice.polytech.al.teamf.entities.User;
import fr.unice.polytech.al.teamf.repositories.NotificationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class UserNotifierBean implements NotifyUser, PullNotifications {

    @Autowired
    NotificationRepository notificationRepository;

    private final Logger logger = LoggerFactory.getLogger(UserNotifierBean.class);

    @Override
    public void notifyUser(User user, String message) {
        Notification notification = new Notification(user, message);
        user.addNotification(notification);
        notificationRepository.save(notification);
        logger.info(String.format("Send message to %s: %s", user.getName(), message));
    }

    @Override
    public List<String> pullNotificationForUser(User user) {
        logger.info(String.format("%s is pulling its notifications", user.getName()));
        List<Notification> res = notificationRepository.findByUser(user);
        user.clearNotifications();
        return res.stream().map(Notification::getMessage).collect(Collectors.toList());
    }
}
