package fr.unice.polytech.al.teamf.usernotifier.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.ElementCollection;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Map;

@Data
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class Answer implements Serializable {
    private String route;
    private String methodName;
    @ElementCollection
    private Map<String, String> parameters;
}
