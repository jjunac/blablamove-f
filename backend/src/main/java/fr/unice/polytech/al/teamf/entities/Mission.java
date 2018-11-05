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
    private User transporter;
    @ManyToOne
    private User owner;

    private Status status;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name="latitude", column= @Column(name="transporterLatitude")),
            @AttributeOverride(name="longitude", column= @Column(name="transporterLongitude"))
    })
    private GPSCoordinate departure;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name="latitude", column= @Column(name="ownerLatitude")),
            @AttributeOverride(name="longitude", column= @Column(name="ownerLongitude"))
    })
    private GPSCoordinate arrival;

    @OneToOne(mappedBy = "mission")
    private Parcel parcel;

    public Mission(User transporter, User owner, GPSCoordinate departure, GPSCoordinate arrival, Parcel parcel) {
        this.transporter = transporter;
        this.owner = owner;
        this.departure = departure;
        this.arrival = arrival;
        this.parcel = parcel;
        this.status = Status.PENDING;
    }

    public int computeRetribution() {
        return (int) (Math.ceil(departure.getDistanceTo(arrival))) * 100;
    }

    public void setOngoing(){
        this.status = Status.ONGOING;
        parcel.setKeeper(transporter);
    }

    public void setFinished(){
        this.status = Status.FINISHED;
    }

    public enum Status {
        PENDING, ONGOING, FINISHED
    }

}
