package fr.unice.polytech.al.teamf;


import java.util.List;

public interface PullNotifications {
    // TODO pass User instead on String when persistance will be operational
    List<String> pullNotificationForUser(String username);

}
