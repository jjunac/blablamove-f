package fr.unice.polytech.al.teamf;

import fr.unice.polytech.al.teamf.entities.Mission;
import fr.unice.polytech.al.teamf.entities.User;

public interface ChangePackageKeeperDriver {
    void takePackage(User newDriver, Mission mission);
}
