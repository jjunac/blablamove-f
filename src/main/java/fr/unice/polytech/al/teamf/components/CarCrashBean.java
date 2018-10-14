package fr.unice.polytech.al.teamf.components;

import fr.unice.polytech.al.teamf.CarCrash;
import fr.unice.polytech.al.teamf.NotifyUser;
import fr.unice.polytech.al.teamf.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CarCrashBean implements CarCrash {

    @Autowired
    NotifyUser notifyUser;

    @Override
    public void notifyUser() {
        notifyUser.notifyUser(new User("Philippe"), "Hello World!");
    }
    
}
