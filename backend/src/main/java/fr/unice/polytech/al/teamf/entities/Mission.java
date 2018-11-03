package fr.unice.polytech.al.teamf.entities;


import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@NoArgsConstructor
public class Mission implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @ManyToOne
    public User transporter;
    @ManyToOne
    public User owner;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name="latitude", column= @Column(name="transporterLatitude")),
            @AttributeOverride(name="longitude", column= @Column(name="transporterLongitude"))
    })
    public GPSCoordinate transporterCoordinate;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name="latitude", column= @Column(name="ownerLatitude")),
            @AttributeOverride(name="longitude", column= @Column(name="ownerLongitude"))
    })
    public GPSCoordinate ownerCoordinate;

    @OneToOne(mappedBy = "mission")
    public Parcel parcel;

    public Mission(User transporter, User owner, Parcel parcel) {
        this.transporter = transporter;
        this.owner = owner;
        this.parcel = parcel;
    }

    public int computeRetribution() {
        return (int) (Math.ceil(transporterCoordinate.getDistanceTo(ownerCoordinate))) * 100;
    }

}
