package fr.unice.polytech.al.teamf.entities;

import lombok.Data;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class User {
    private final String name;
    private int points = 0;
    private List <Parcel> transportedPackages = new LinkedList <>();

    public boolean addTransportedPackage(Parcel parcel) {
        return transportedPackages.add(parcel);
    }

    public boolean removeTransportedPackage(Parcel parcel) {
        return transportedPackages.remove(parcel);
    }

    public List <User> getUsersOwningPackages() {
        return transportedPackages.stream().map(Parcel::getOwner).distinct().collect(Collectors.toList());
    }

    public void addPoints(int nbPoints) {
        points += nbPoints;
    }

    public int getPoints() {
        return points;
    }
}
