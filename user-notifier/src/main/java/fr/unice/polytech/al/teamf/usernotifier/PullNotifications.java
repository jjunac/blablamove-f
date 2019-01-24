package fr.unice.polytech.al.teamf.usernotifier;


import fr.unice.polytech.al.teamf.usernotifier.entities.Notification;
import fr.unice.polytech.al.teamf.usernotifier.entities.User;

import java.util.List;

public interface PullNotifications {
    List<Notification> pullNotificationForUser(User user);

}
