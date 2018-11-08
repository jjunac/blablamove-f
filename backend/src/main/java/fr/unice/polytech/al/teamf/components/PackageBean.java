package fr.unice.polytech.al.teamf.components;

import fr.unice.polytech.al.teamf.ManagePackage;
import fr.unice.polytech.al.teamf.NotifyUser;
import fr.unice.polytech.al.teamf.entities.GPSCoordinate;
import fr.unice.polytech.al.teamf.entities.Mission;
import fr.unice.polytech.al.teamf.entities.Parcel;
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
    public boolean dropPackageToHost(User host, Parcel parcel) {
        log.trace("PackageBean.dropPackage");
        parcel.setKeeper(host);
        notifyUser.notifyUser(parcel.getOwner(), buildDroppedPackageMessage(host.getName()));
        return true;
    }

    @Override
    public boolean takePackageFromHost(User newDriver, Parcel parcel) {
        log.trace("PackageBean.takePackage");
        // We dont care about coordinates here
        Mission mission = new Mission(newDriver, parcel.getOwner(), new GPSCoordinate(42,42), new GPSCoordinate(42,42), parcel);
        mission.setOngoing();
        notifyUser.notifyUser(mission.getParcel().getOwner(), buildTakenPackageMessage(newDriver.getName()));
        return newDriver.addTransportedMission(mission);
    }

    @Override
    public boolean takePackageFromDriver(User newDriver, Mission mission) {
        log.trace("PackageBean.takePackage");
        mission.setOngoing();
        notifyUser.notifyUser(mission.getOwner(), buildChangeDriverMessage(newDriver.getName()));
        return true;
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
