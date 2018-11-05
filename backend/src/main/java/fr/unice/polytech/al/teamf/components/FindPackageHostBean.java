package fr.unice.polytech.al.teamf.components;

import fr.unice.polytech.al.teamf.FindPackageHost;
import fr.unice.polytech.al.teamf.NotifyUser;
import fr.unice.polytech.al.teamf.entities.Mission;
import fr.unice.polytech.al.teamf.entities.Parcel;
import fr.unice.polytech.al.teamf.entities.User;
import fr.unice.polytech.al.teamf.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class FindPackageHostBean implements FindPackageHost {

    @Autowired
    NotifyUser notifyUser;
    @Autowired
    UserRepository userRepository;

    @Override
    public User findHost(Parcel mission) {
        log.trace("FindPackageHostBean.findHost");
        // Mocking new Host user
        User newHost = userRepository.findByName("Julien").get(0);
        notifyUser.notifyUser(mission.getOwner(), buildOwnerMessage(newHost.getName()));
        notifyUser.notifyUser(newHost, buildHostMessage(mission.getOwner().getName()));
        return newHost;
    }

    @Override
    public void dropPackage(User host, Mission mission) {
        log.trace("FindPackageHostBean.dropPackage");
        mission.setTransporter(null);
        mission.getParcel().setKeeper(host);
        notifyUser.notifyUser(mission.getParcel().getOwner(), buildDroppedPackageMessage(host.getName()));
    }

    @Override
    public void takePackage(User newDriver, Mission mission) {
        log.trace("FindPackageHostBean.dropPackage");
        mission.setTransporter(newDriver);
        mission.getParcel().setKeeper(newDriver);
        notifyUser.notifyUser(mission.getParcel().getOwner(), buildTakenPackageMessage(newDriver.getName()));
    }

    static String buildOwnerMessage(String hostName) {
        return String.format("%s will host your package until a new transporter arrives !", hostName);
    }

    static String buildHostMessage(String ownerName) {
        return String.format("You will be hosting %s's package !", ownerName);
    }

    static String buildDroppedPackageMessage(String hostName) {
        return String.format("Your package has been dropped to %s's house !", hostName);
    }

    static String buildTakenPackageMessage(String newDriverName) {
        return String.format("Your package has been taken by %s from the temporary location !", newDriverName);
    }
}
