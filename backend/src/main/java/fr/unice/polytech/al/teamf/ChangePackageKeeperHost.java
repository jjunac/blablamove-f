package fr.unice.polytech.al.teamf;

import fr.unice.polytech.al.teamf.entities.Mission;
import fr.unice.polytech.al.teamf.entities.User;

public interface ChangePackageKeeperHost {
    void dropPackage(User host, Mission mission);

    void takePackage(User newHost, Mission mission);
}
