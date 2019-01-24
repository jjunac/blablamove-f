package fr.unice.polytech.al.teamf.usernotifier.exceptions;

public class UnknownUserException extends RuntimeException {

    public UnknownUserException(String username) {
        super(String.format("Unknown user %s", username));
    }
}
