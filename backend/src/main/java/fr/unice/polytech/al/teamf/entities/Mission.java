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

    @ManyToOne
    User transporter;
    @ManyToOne
    User owner;

    @Embedded
    GPSCoordinate transporterCoordinate;
    @Embedded
    GPSCoordinate ownerCoordinate;

    @OneToOne(mappedBy = "mission")
    Parcel parcel;

    public int computeRetribution() {
        return (int) (Math.ceil(transporterCoordinate.getDistanceTo(ownerCoordinate))) * 100;
    }

}
