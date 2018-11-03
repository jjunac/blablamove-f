package fr.unice.polytech.al.teamf;


import fr.unice.polytech.al.teamf.entities.Answer;
import fr.unice.polytech.al.teamf.entities.User;

public interface NotifyUser {

    void notifyUser(User user, String message);
    void notifyUserWithAnswer(User user, String message, Answer answer);

}
