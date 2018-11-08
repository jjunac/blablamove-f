package fr.unice.polytech.al.teamf.components;

import fr.unice.polytech.al.teamf.ComputePoints;
import fr.unice.polytech.al.teamf.ManagePackage;
import fr.unice.polytech.al.teamf.NotifyUser;
import fr.unice.polytech.al.teamf.entities.GPSCoordinate;
import fr.unice.polytech.al.teamf.entities.Mission;
import fr.unice.polytech.al.teamf.entities.Parcel;
import fr.unice.polytech.al.teamf.entities.User;
import fr.unice.polytech.al.teamf.exceptions.UnknownUserException;
import fr.unice.polytech.al.teamf.repositories.MissionRepository;
import fr.unice.polytech.al.teamf.repositories.ParcelRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.file.Files;

@Slf4j
@Component
public class PackageBean implements ManagePackage {

    @Autowired
    ComputePoints computePoints;
    @Autowired
    NotifyUser notifyUser;
    @Autowired
    MissionRepository missionRepository;
    @Autowired
    ParcelRepository parcelRepository;

    @Override
    public boolean missionFinished(Mission mission) {
        try {
            computePoints.computePoints(mission);
            mission.setFinished(); // useless for the moment, but may be useful if we want to keep a history
            mission.getTransporter().removeTransportedMission(mission);
            mission.getOwner().removeOwnedMission(mission);
            parcelRepository.delete(mission.getParcel());
            missionRepository.delete(mission);
            return true;
        } catch (UnknownUserException e) {
            log.error(e.getMessage());
        }
        return false;
    }

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
        missionRepository.save(mission);
        parcel.setMission(mission);
        parcel.setKeeper(newDriver);
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
