package fr.unice.polytech.al.teamf;


import fr.unice.polytech.al.teamf.entities.User;

import java.util.List;

public interface PullNotifications {
    List<String> pullNotificationForUser(User user);

}
