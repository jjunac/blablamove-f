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

    @OneToMany(mappedBy = "transporter")
    private List <Parcel> transportedPackages = new LinkedList <>();

    @OneToMany(mappedBy = "owner")
    private List <Parcel> ownedPackages = new LinkedList <>();

    private String name;

    public User(String name) {
        this.name = name;
    }

    public boolean addTransportedPackage(Parcel parcel) {
        parcel.setTransporter(this);
        return transportedPackages.add(parcel);
    }

    public boolean removeTransportedPackage(Parcel parcel) {
        return transportedPackages.remove(parcel);
    }

    public List <User> getUsersOwningPackages() {
        return transportedPackages.stream().map(Parcel::getOwner).distinct().collect(Collectors.toList());
    }

    public boolean addOwnedPackage(Parcel parcel) {
        return ownedPackages.add(parcel);
    }

    public boolean removeOwnedPackage(Parcel parcel) {
        return ownedPackages.remove(parcel);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("User{");
        sb.append("id=").append(id);
        sb.append(", transportedPackages=").append(transportedPackages.stream().map(parcel -> parcel.getOwner().getName() + "'s parcel").collect(Collectors.toList()));
        sb.append(", name='").append(name).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
