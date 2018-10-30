package fr.unice.polytech.al.teamf;

import fr.unice.polytech.al.teamf.components.NotifyCarCrashBean;
import fr.unice.polytech.al.teamf.entities.Parcel;
import fr.unice.polytech.al.teamf.entities.User;
import fr.unice.polytech.al.teamf.repositories.ParcelRepository;
import fr.unice.polytech.al.teamf.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application implements CommandLineRunner {

    @Autowired
    UserRepository userRepository;
    @Autowired
    ParcelRepository parcelRepository;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Override
    public void run(String... arg0) throws Exception {
        User thomas = new User("Thomas");
        userRepository.save(thomas);
        User loic = new User("Loic");
        userRepository.save(loic);
        User jeremy = new User("Jeremy");
        userRepository.save(jeremy);
        User johann = new User("Johann");
        Parcel parcel1 = new Parcel(jeremy);
        parcelRepository.save(parcel1);
        johann.addTransportedPackage(parcel1);
        Parcel parcel2 = new Parcel(thomas);
        johann.addTransportedPackage(parcel2);
        parcelRepository.save(parcel2);
        userRepository.save(johann);
    }

}
