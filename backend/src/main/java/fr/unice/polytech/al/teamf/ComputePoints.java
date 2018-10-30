package fr.unice.polytech.al.teamf;

import fr.unice.polytech.al.teamf.exceptions.UnknownUserException;
import fr.unice.polytech.al.teamf.entities.Mission;
import fr.unice.polytech.al.teamf.entities.User;

public interface ComputePoints {

    int computePoints(User user, Mission mission) throws UnknownUserException;

}
