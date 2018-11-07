package fr.unice.polytech.al.teamf.components;

import fr.unice.polytech.al.teamf.ManagePackage;
import fr.unice.polytech.al.teamf.NotifyUser;
import fr.unice.polytech.al.teamf.entities.Mission;
import fr.unice.polytech.al.teamf.entities.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PackageBean implements ManagePackage {
    @Autowired
    NotifyUser notifyUser;


    @Override
    public void dropPackageToHost(User host, Mission mission) {
        log.trace("PackageBean.dropPackage");
        mission.setTransporter(null);
        mission.getParcel().setKeeper(host);
        notifyUser.notifyUser(mission.getParcel().getOwner(), buildDroppedPackageMessage(host.getName()));
    }

    @Override
    public void takePackageFromHost(User newHost, Mission mission) {
        log.trace("PackageBean.takePackage");
        mission.setTransporter(newHost);
        mission.getParcel().setKeeper(newHost);
        notifyUser.notifyUser(mission.getParcel().getOwner(), buildTakenPackageMessage(newHost.getName()));
    }

    @Override
    public void takePackageFromDriver(User newDriver, Mission mission) {
        log.trace("PackageBean.takePackage");
        notifyUser.notifyUser(mission.getOwner(), buildChangeDriverMessage(newDriver.getName()));
    }

    static String buildDroppedPackageMessage(String hostName) {
        return String.format("Your package has been dropped to %s's house !", hostName);
    }

    static String buildTakenPackageMessage(String newDriverName) {
        return String.format("Your package has been taken by %s from the temporary location !", newDriverName);
    }

    static String buildChangeDriverMessage(String newDriverName) {
        return String.format("%s has taken your package !", newDriverName);

    }
}
