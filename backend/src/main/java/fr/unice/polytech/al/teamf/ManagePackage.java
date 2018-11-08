package fr.unice.polytech.al.teamf;

import fr.unice.polytech.al.teamf.entities.Mission;
import fr.unice.polytech.al.teamf.entities.Parcel;
import fr.unice.polytech.al.teamf.entities.User;

public interface ManagePackage {
    boolean dropPackageToHost(User host, Parcel parcel);

    boolean takePackageFromHost(User newHost, Parcel parcel);

    boolean takePackageFromDriver(User newDriver, Mission mission);
}
