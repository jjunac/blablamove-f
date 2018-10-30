package fr.unice.polytech.al.teamf;

import fr.unice.polytech.al.teamf.exceptions.UnknownUserException;
import fr.unice.polytech.al.teamf.entities.Mission;

public interface ComputePoints {

    int computePoints(Mission mission) throws UnknownUserException;

}
