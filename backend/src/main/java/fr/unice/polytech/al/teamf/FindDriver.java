package fr.unice.polytech.al.teamf;

import fr.unice.polytech.al.teamf.entities.GPSCoordinate;
import fr.unice.polytech.al.teamf.entities.Mission;
import fr.unice.polytech.al.teamf.entities.User;

import java.util.Optional;

public interface FindDriver {

    User findNewDriver(User currentDriver, Mission mission, GPSCoordinate coordinate);

    void answerToPendingMission(Mission mission, User driver, boolean answer);
}
