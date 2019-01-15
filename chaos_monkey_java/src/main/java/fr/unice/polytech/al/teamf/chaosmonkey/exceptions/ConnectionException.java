package fr.unice.polytech.al.teamf.chaosmonkey.exceptions;

public class ConnectionException extends Exception {
    public ConnectionException(Throwable cause) {
        super("Cannot connect to Chaos Monkey", cause);
    }
}
