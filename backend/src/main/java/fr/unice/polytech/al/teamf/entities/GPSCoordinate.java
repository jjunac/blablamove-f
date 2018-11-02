package fr.unice.polytech.al.teamf.entities;

import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class GPSCoordinate implements Serializable {

    double latitude;
    double longitude;

    double getDistanceTo(GPSCoordinate coordinate) {
        return Math.sqrt(Math.pow(latitude - coordinate.latitude, 2) + Math.pow(longitude - coordinate.longitude, 2));
    }
}
