package fr.unice.polytech.al.teamf;

import fr.unice.polytech.al.teamf.entities.Mission;
import fr.unice.polytech.al.teamf.entities.User;

public interface ManagePackage {
    void dropPackageToHost(User host, Mission mission);

    void takePackageFromHost(User newHost, Mission mission);

    void takePackageFromDriver(User newDriver, Mission mission);
}
