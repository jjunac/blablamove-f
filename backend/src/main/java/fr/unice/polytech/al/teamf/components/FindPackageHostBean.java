package fr.unice.polytech.al.teamf.components;

import fr.unice.polytech.al.teamf.FindPackageHost;
import fr.unice.polytech.al.teamf.NotifyUser;
import fr.unice.polytech.al.teamf.entities.Mission;
import fr.unice.polytech.al.teamf.entities.Parcel;
import fr.unice.polytech.al.teamf.entities.User;
import fr.unice.polytech.al.teamf.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FindPackageHostBean implements FindPackageHost {

    @Autowired
    NotifyUser notifyUser;
    @Autowired
    UserRepository userRepository;

    @Override
    public User findHost(Mission mission) {
        // Mocking new Host user
        User newHost = userRepository.findByName("Julien").get(0);
        notifyUser.notifyUser(mission.getOwner(), buildOwnerMessage(newHost.getName()));
        notifyUser.notifyUser(newHost, buildHostMessage(mission.getOwner().getName()));
        return newHost;
    }

    static String buildOwnerMessage(String hostName) {
        return String.format("%s will host your package until a new transporter arrives !", hostName);
    }

    static String buildHostMessage(String ownerName) {
        return String.format("You will be hosting %s's package !", ownerName);
    }
}
