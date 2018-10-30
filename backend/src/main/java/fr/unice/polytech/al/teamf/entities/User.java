package fr.unice.polytech.al.teamf.entities;

import lombok.Data;
import lombok.NoArgsConstructor;

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

}
