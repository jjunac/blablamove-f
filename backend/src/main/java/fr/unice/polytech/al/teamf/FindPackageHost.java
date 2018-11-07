package fr.unice.polytech.al.teamf;

import fr.unice.polytech.al.teamf.entities.GPSCoordinate;
import fr.unice.polytech.al.teamf.entities.Mission;
import fr.unice.polytech.al.teamf.entities.Parcel;
import fr.unice.polytech.al.teamf.entities.User;

public interface FindPackageHost {

    User findHost(Parcel parcel);

    void dropPackage(User host, Mission mission);

    void takePackage(User newDriver, Mission mission);

}
