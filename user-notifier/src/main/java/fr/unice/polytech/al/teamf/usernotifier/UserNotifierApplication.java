package fr.unice.polytech.al.teamf.usernotifier;

import fr.unice.polytech.al.teamf.usernotifier.entities.User;
import fr.unice.polytech.al.teamf.usernotifier.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class UserNotifierApplication implements CommandLineRunner {
    
    @Autowired
    UserRepository userRepository;
    
    public static void main(String[] args) {
        SpringApplication.run(UserNotifierApplication.class, args);
    }
    
    @Override
    public void run(String... args) throws Exception {
        User thomas = new User("lol");
        userRepository.save(thomas);
        
    }
}

