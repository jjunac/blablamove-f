package fr.unice.polytech.al.teamf.usernotifier.components;

import fr.unice.polytech.al.teamf.usernotifier.NotifyUser;
import fr.unice.polytech.al.teamf.usernotifier.PullNotifications;
import fr.unice.polytech.al.teamf.usernotifier.entities.Answer;
import fr.unice.polytech.al.teamf.usernotifier.entities.Notification;
import fr.unice.polytech.al.teamf.usernotifier.entities.User;
import fr.unice.polytech.al.teamf.usernotifier.exceptions.UnknownUserException;
import fr.unice.polytech.al.teamf.usernotifier.repositories.NotificationRepository;
import fr.unice.polytech.al.teamf.usernotifier.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.List;

@Slf4j
@Component
public class UserNotifierBean implements NotifyUser, PullNotifications {
    
    private NotificationRepository notificationRepository;
    private UserRepository userRepository;
    
    @Autowired
    public UserNotifierBean(NotificationRepository notificationRepository, UserRepository userRepository) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
    }
    
    @Override
    public void notifyUser(String username, String message) {
        User user = userRepository.findByName(username).stream().findFirst().orElseThrow(() -> new UnknownUserException(username));
        sendNotification(new Notification(user, message, null));
        
        
    }
    
    @Override
    public void notifyUserWithAnswer(String username, String message, Answer answer) {
        User user = userRepository.findByName(username).stream().findFirst().orElseThrow(() -> new UnknownUserException(username));
        sendNotification(new Notification(user, message, answer));
    }
    
    private void sendNotification(Notification notification) {
        notification.getUser().addNotification(notification);
        notificationRepository.save(notification);
        log.info(String.format("Send message to %s: %s", notification.getUser().getName(), notification.getMessage()));
    }
    
    @Transactional
    @Override
    public List<Notification> pullNotificationForUser(User user) {
        log.info(String.format("%s is pulling its notifications", user.getName()));
        List<Notification> notifications = notificationRepository.findByUser(user);
        user.clearNotifications();
        return notifications;
    }
    
}
