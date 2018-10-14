package fr.unice.polytech.al.teamf.components;

import fr.unice.polytech.al.teamf.NotifyUser;
import fr.unice.polytech.al.teamf.User;
import org.springframework.stereotype.Component;

@Component
public class UserNotifierBean implements NotifyUser {

    @Override
    public void notifyUser(User user, String message) {
        System.out.println(String.format("Send message to %s: %s", user.getName(), message));
    }

}
