package fr.unice.polytech.al.teamf.components;

import fr.unice.polytech.al.teamf.ComputePoints;
import fr.unice.polytech.al.teamf.entities.Mission;
import fr.unice.polytech.al.teamf.entities.User;
import org.springframework.stereotype.Component;

@Component
public class AccountingBean implements ComputePoints {

    @Override
    public int compute(User user, Mission mission) {
        return mission.getRetribution();
    }

}
