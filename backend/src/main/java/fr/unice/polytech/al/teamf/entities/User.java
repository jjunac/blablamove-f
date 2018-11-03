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
    private long id;

    @OneToMany(mappedBy = "transporter")
    private List <Mission> transportedMissions = new LinkedList <>();

    @OneToMany(mappedBy = "owner")
    private List <Mission> ownedMissions = new LinkedList <>();

    @OneToMany(mappedBy = "host")
    private List <Parcel> hostedPackages = new LinkedList <>();

    @OneToMany(mappedBy = "owner")
    private List <Parcel> ownedPackages = new LinkedList <>();

    @OneToMany(mappedBy = "user")
    private List <Notification> notifications = new LinkedList <>();

    private String name;

    public User(String name) {
        this.name = name;
    }

    public boolean addTransportedMission(Mission mission) {
        mission.setTransporter(this);
        return transportedMissions.add(mission);
    }

    public boolean removeTransportedMission(Mission mission) {
        return transportedMissions.remove(mission);
    }

    public List <User> getUsersOwningMissions() {
        return transportedMissions.stream().map(Mission::getOwner).distinct().collect(Collectors.toList());
    }

    public boolean addOwnedMission(Mission mission) {
        return ownedMissions.add(mission);
    }

    public boolean removeOwnedMission(Mission mission) {
        return ownedMissions.remove(mission);
    }

    public boolean addHostedPackage(Parcel parcel) {
        parcel.setHost(this);
        return hostedPackages.add(parcel);
    }

    public boolean removeHostedPackage(Parcel parcel) {
        return hostedPackages.remove(parcel);
    }

    public List <User> getUsersOwningPackages() {
        return hostedPackages.stream().map(Parcel::getOwner).distinct().collect(Collectors.toList());
    }

    public boolean addOwnedPackage(Parcel parcel) {
        return ownedPackages.add(parcel);
    }

    public boolean removeOwnedPackage(Parcel parcel) {
        return ownedPackages.remove(parcel);
    }

    public boolean addNotification(Notification notification) {
        return notifications.add(notification);
    }

    public void clearNotifications() {
        notifications.clear();
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("User{");
        sb.append("id=").append(id);
        sb.append(", transportedMissions=").append(transportedMissions.stream().map(mission -> mission.getOwner().getName() + "'s mission").collect(Collectors.toList()));
        sb.append(", name='").append(name).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
