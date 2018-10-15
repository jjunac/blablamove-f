package fr.unice.polytech.al.teamf.entities;


import fr.unice.polytech.al.teamf.entities.User;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Parcel {
    User owner;
}
