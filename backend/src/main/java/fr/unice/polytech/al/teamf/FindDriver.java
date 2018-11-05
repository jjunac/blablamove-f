package fr.unice.polytech.al.teamf;

import fr.unice.polytech.al.teamf.entities.GPSCoordinate;
import fr.unice.polytech.al.teamf.entities.Mission;
import fr.unice.polytech.al.teamf.entities.Parcel;
import fr.unice.polytech.al.teamf.entities.User;

public interface FindDriver {

    boolean answerToPendingMission(Mission mission, User driver, boolean answer);

    User findNewDriver(User currentDriver, Parcel parcel, GPSCoordinate coordinate, GPSCoordinate arrival);

    void takePackage(User newDriver, Mission mission);
}
