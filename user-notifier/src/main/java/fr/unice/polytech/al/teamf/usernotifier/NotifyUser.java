package fr.unice.polytech.al.teamf.usernotifier;


import fr.unice.polytech.al.teamf.usernotifier.entities.Answer;

public interface NotifyUser {

    void notifyUser(String username, String message);
    void notifyUserWithAnswer(String username, String message, Answer answer);

}
