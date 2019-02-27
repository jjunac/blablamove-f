package fr.unice.polytech.al.teamf;

public class UnknownUserException extends Throwable {

    public UnknownUserException(String user) {
        super(String.format("Cannot add points to user %s : unknown user", user));
    }
}
