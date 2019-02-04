package fr.unice.polytech.al.teamf;

import fr.unice.polytech.al.teamf.entities.Mission;
import fr.unice.polytech.al.teamf.entities.User;

public interface AnswerMission {

    boolean answerToPendingMission(long missionId, String newDriverName, boolean answer);

}
