package fr.unice.polytech.al.teamf;

import fr.unice.polytech.al.teamf.entities.Parcel;
import fr.unice.polytech.al.teamf.entities.User;
import fr.unice.polytech.al.teamf.repositories.ParcelRepository;
import fr.unice.polytech.al.teamf.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class IntegrationTest {

    @Autowired
    protected UserRepository userRepository;
    @Autowired
    protected ParcelRepository parcelRepository;

    public User createAndSaveUser(String name) {
        User user = new User(name);
        userRepository.save(user);
        return user;
    }

    public Parcel createAndSaveParcel(User owner, User transporter) {
        Parcel parcel = new Parcel(owner);
        transporter.addTransportedPackage(parcel);
        parcelRepository.save(parcel);
        return parcel;
    }

}
