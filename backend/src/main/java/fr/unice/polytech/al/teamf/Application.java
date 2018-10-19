package fr.unice.polytech.al.teamf;

import fr.unice.polytech.al.teamf.components.NotifyCarCrashBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {

    @Autowired
    static NotifyCarCrashBean carCrashBean;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
