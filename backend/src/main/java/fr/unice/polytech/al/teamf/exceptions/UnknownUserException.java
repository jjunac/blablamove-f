package fr.unice.polytech.al.teamf.exceptions;

import fr.unice.polytech.al.teamf.entities.User;

public class UnknownUserException extends Throwable {

    public UnknownUserException(User user) {
        super(String.format("Cannot add points to user %s : unknown user", user.getName()));
    }
}
