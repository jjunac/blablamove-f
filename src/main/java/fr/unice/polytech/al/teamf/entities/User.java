package fr.unice.polytech.al.teamf.entities;

import fr.unice.polytech.al.teamf.Parcel;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

@Data
public class User {
    private final String name;
    private List<Parcel> transportedPackages;

    public List<User> getUsersOwningPackages(){
        return transportedPackages.stream().map(Parcel::getOwner).distinct().collect(Collectors.toList());
    }

}
