package fr.unice.polytech.al.teamf.components;

import fr.unice.polytech.al.teamf.NotifyUser;
import fr.unice.polytech.al.teamf.PullNotifications;
import fr.unice.polytech.al.teamf.entities.Answer;
import fr.unice.polytech.al.teamf.entities.Notification;
import fr.unice.polytech.al.teamf.entities.User;
import fr.unice.polytech.al.teamf.repositories.NotificationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class UserNotifierBean implements NotifyUser, PullNotifications {

    @Autowired
    NotificationRepository notificationRepository;

    @Override
    public void notifyUser(User user, String message) {
        sendNotification(new Notification(user, message, null));
    }

    @Override
    public void notifyUserWithAnswer(User user, String message, Answer answer) {
        sendNotification(new Notification(user, message, answer));
    }

    private void sendNotification(Notification notification) {
        notification.getUser().addNotification(notification);
        notificationRepository.save(notification);
        log.info(String.format("Send message to %s: %s", notification.getUser().getName(), notification.getMessage()));
    }

    @Override
    public List<Notification> pullNotificationForUser(User user) {
        log.info(String.format("%s is pulling its notifications", user.getName()));
        List<Notification> res = notificationRepository.findByUser(user);
        user.clearNotifications();
        return res;
    }

}
