package fr.unice.polytech.al.teamf;

import fr.unice.polytech.al.teamf.entities.Parcel;
import fr.unice.polytech.al.teamf.entities.User;

public interface AnswerPackageHosting {

    boolean answerToPendingPackageHosting(Parcel parcel, User user, boolean answer);
}
