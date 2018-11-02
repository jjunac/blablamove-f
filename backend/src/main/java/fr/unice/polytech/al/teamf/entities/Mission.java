package fr.unice.polytech.al.teamf.entities;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Mission implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    User driver;
    User owner;
    @Embedded
    GPSCoordinate driverCoordinate;
    @Embedded
    GPSCoordinate ownerCoordinate;

    @OneToMany(mappedBy = "mission")
    List <Parcel> parcels = new LinkedList <>();

    public int computeRetribution() {
        return (int) (Math.ceil(driverCoordinate.getDistanceTo(ownerCoordinate))) * 100;
    }

}
