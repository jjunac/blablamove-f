package fr.unice.polytech.al.teamf.entities;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Entity
@NoArgsConstructor
public class User implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    @OneToMany(mappedBy = "owner")
    private List <Parcel> transportedPackages = new LinkedList <>();

    private String name;
    private int points = 0;

    public User(String name) {
        this.name = name;
    }

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
