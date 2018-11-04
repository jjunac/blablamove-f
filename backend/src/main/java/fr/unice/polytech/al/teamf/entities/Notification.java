package fr.unice.polytech.al.teamf.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@NoArgsConstructor
public class Notification implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @ManyToOne
    @JsonIgnore
    private User user;
    @ManyToOne
    @JsonIgnore
    private User askedUser;

    private String message;

    @Embedded
    private Answer answer;

    public Notification(User user, String message, Answer answer) {
        this.user = user;
        this.message = message;
        this.answer = answer;
        this.askedUser = user;
    }
}
