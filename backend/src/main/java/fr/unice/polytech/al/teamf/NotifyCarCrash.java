package fr.unice.polytech.al.teamf;

import fr.unice.polytech.al.teamf.entities.GPSCoordinate;
import fr.unice.polytech.al.teamf.entities.User;

public interface NotifyCarCrash {

    void notifyCrash(User user, GPSCoordinate coordinate);

}
