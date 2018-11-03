package fr.unice.polytech.al.teamf;

import fr.unice.polytech.al.teamf.entities.GPSCoordinate;
import fr.unice.polytech.al.teamf.entities.Mission;
import fr.unice.polytech.al.teamf.entities.Parcel;
import fr.unice.polytech.al.teamf.entities.User;
import fr.unice.polytech.al.teamf.repositories.MissionRepository;
import fr.unice.polytech.al.teamf.repositories.ParcelRepository;
import fr.unice.polytech.al.teamf.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class IntegrationTest {

    @Autowired
    protected UserRepository userRepository;
    @Autowired
    protected ParcelRepository parcelRepository;
    @Autowired
    protected MissionRepository missionRepository;

    public User createAndSaveUser(String name) {
        User user = new User(name);
        userRepository.save(user);
        return user;
    }

    public Parcel createAndSaveParcel(User owner) {
        Parcel parcel = new Parcel(owner);
        parcelRepository.save(parcel);
        return parcel;
    }

        public Mission createAndSaveMissionWithParcel(User owner, User transporter, GPSCoordinate departure, GPSCoordinate arrival) {
        // FIXME pass arguments to parcel
        Parcel parcel = new Parcel();
        Mission mission = new Mission(transporter, owner, departure, arrival, parcel);
        transporter.addTransportedMission(mission);
        parcelRepository.save(parcel);
        missionRepository.save(mission);
        return mission;
    }

}
