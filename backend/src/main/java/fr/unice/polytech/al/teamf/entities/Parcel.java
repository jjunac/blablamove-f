package fr.unice.polytech.al.teamf.entities;


import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@NoArgsConstructor
public class Parcel implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @ManyToOne
    private User owner;
    @ManyToOne
    private User host;
    @ManyToOne
    private Mission mission;

    public Parcel(User owner) {
        this.owner = owner;
    }

}
