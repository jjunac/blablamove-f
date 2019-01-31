package fr.unice.polytech.al.teamf.usernotifier.entities;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
public class User implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
    private List <Notification> notifications = new LinkedList <>();

    private String name;

    public User(String name) {
        this.name = name;
    }

    public boolean addNotification(Notification notification) {
        return notifications.add(notification);
    }

    public void clearNotifications() {
        for(Iterator<Notification> notificationIterator = notifications.iterator();
            notificationIterator.hasNext(); ) {
            Notification notification = notificationIterator.next();
            notification.setUser(null);
            notificationIterator.remove();
        }
    }

    @Override
    public String toString() {
        return "User{" + "id=" + id +
                ", notifications=" + notifications.stream().map(Notification::getMessage) +
                ", name='" + name + '\'' +
                '}';
    }

}
