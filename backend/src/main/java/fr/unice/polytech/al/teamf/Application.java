package fr.unice.polytech.al.teamf;

import fr.unice.polytech.al.teamf.components.NotifyCarCrashBean;
import fr.unice.polytech.al.teamf.entities.Mission;
import fr.unice.polytech.al.teamf.entities.Parcel;
import fr.unice.polytech.al.teamf.entities.User;
import fr.unice.polytech.al.teamf.repositories.ParcelRepository;
import fr.unice.polytech.al.teamf.repositories.UserRepository;
import fr.unice.polytech.al.teamf.webservices.IncidentServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application implements CommandLineRunner {

    private final Logger logger = LoggerFactory.getLogger(getClass());


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
        userRepository.save(johann);
        User erick = new User("Erick");
        userRepository.save(erick);
        User julien = new User("Julien");
        userRepository.save(julien);

        Parcel parcel1 = new Parcel();
        johann.addTransportedMission(new Mission(johann, jeremy, parcel1));
        parcelRepository.save(parcel1);
        Parcel parcel2 = new Parcel();
        johann.addTransportedMission(new Mission(johann, thomas, parcel2));
        parcelRepository.save(parcel2);

        logger.debug(johann.toString());
        logger.debug(parcel1.toString());
        logger.debug(parcel2.toString());

    }

}
