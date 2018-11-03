package fr.unice.polytech.al.teamf;


import fr.unice.polytech.al.teamf.entities.Notification;
import fr.unice.polytech.al.teamf.entities.User;

import java.util.List;

public interface PullNotifications {
    List<Notification> pullNotificationForUser(User user);

}
