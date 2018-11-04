package fr.unice.polytech.al.teamf;

import fr.unice.polytech.al.teamf.entities.GPSCoordinate;
import fr.unice.polytech.al.teamf.entities.Mission;
import fr.unice.polytech.al.teamf.entities.User;

public interface FindDriver {

    User findNewDriver(User currentDriver, Mission mission, GPSCoordinate coordinate);

    boolean answerToPendingMission(Mission mission, User driver, boolean answer);
}
