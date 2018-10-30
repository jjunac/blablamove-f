package fr.unice.polytech.al.teamf.components;

import fr.unice.polytech.al.teamf.FindPackageHost;
import fr.unice.polytech.al.teamf.NotifyUser;
import fr.unice.polytech.al.teamf.entities.Parcel;
import fr.unice.polytech.al.teamf.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FindPackageHostBean implements FindPackageHost {
    @Autowired
    NotifyUser notifyUser;

    @Override
    public User findHost(Parcel parcel) {
        // Mocking new Host user
        User newHost = new User("Camille");
        notifyUser.notifyUser(parcel.getOwner(), buildOwnerMessage(newHost.getName()));
        notifyUser.notifyUser(newHost, buildHostMessage(parcel.getOwner().getName()));
        return newHost;
    }

    static String buildOwnerMessage(String hostName) {
        return String.format("%s will host your package until a new driver arrives !", hostName);
    }

    static String buildHostMessage(String ownerName) {
        return String.format("You will be hosting %s's package !", ownerName);
    }
}
