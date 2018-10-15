package fr.unice.polytech.al.teamf.components;

import fr.unice.polytech.al.teamf.NotifyUser;
import fr.unice.polytech.al.teamf.entities.User;
import org.springframework.stereotype.Component;

import java.io.PrintStream;

@Component
public class UserNotifierBean implements NotifyUser {

    PrintStream printStream = System.out;

    @Override
    public void notifyUser(User user, String message) {
        printStream.println(String.format("Send message to %s: %s", user.getName(), message));
    }

}
