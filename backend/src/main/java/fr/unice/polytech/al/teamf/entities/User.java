package fr.unice.polytech.al.teamf.entities;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Iterator;
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

    @OneToMany(mappedBy = "keeper")
    private List <Parcel> keepedPackages = new LinkedList <>();

    @OneToMany(mappedBy = "owner")
    private List <Parcel> ownedPackages = new LinkedList <>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
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

    public List <Mission> getTransportedMissionsWithStatus(Mission.Status status) {
        return transportedMissions.stream()
                .filter(mission -> mission.getStatus().equals(status))
                .collect(Collectors.toList());
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

    public boolean addKeepedPackage(Parcel parcel) {
        parcel.setKeeper(this);
        return keepedPackages.add(parcel);
    }

    public boolean removeKeepedPackage(Parcel parcel) {
        return keepedPackages.remove(parcel);
    }

    public List <User> getUsersOwningPackages() {
        return keepedPackages.stream().map(Parcel::getOwner).distinct().collect(Collectors.toList());
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
        for(Iterator<Notification> notificationIterator = notifications.iterator();
            notificationIterator.hasNext(); ) {
            Notification notification = notificationIterator .next();
            notification.setUser(null);
            notificationIterator.remove();
        }
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("User{");
        sb.append("id=").append(id);
        sb.append(", transportedMissions=").append(transportedMissions.stream().map(User::missionToString).collect(Collectors.toList()));
        sb.append(", ownedMissions=").append(ownedMissions.stream().map(User::missionToString).collect(Collectors.toList()));
        sb.append(", keepedPackages=").append(keepedPackages.stream().map(User::pacakgeToString).collect(Collectors.toList()));
        sb.append(", ownedPackages=").append(ownedPackages.stream().map(User::pacakgeToString).collect(Collectors.toList()));
        sb.append(", notifications=").append(notifications.stream().map(Notification::getMessage));
        sb.append(", name='").append(name).append('\'');
        sb.append('}');
        return sb.toString();
    }

    private static String missionToString(Mission mission) {
        return mission.getOwner().getName() + "'s mission";
    }

    private static String pacakgeToString(Parcel parcel) {
        return parcel.getOwner().getName() + "'s mission";
    }
}
