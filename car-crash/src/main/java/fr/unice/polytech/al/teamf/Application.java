package fr.unice.polytech.al.teamf;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class Application {

    @Autowired
    static CarCrash carCrash;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
        carCrash.notifyUser();
    }

}
