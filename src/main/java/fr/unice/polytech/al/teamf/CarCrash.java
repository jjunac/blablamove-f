package fr.unice.polytech.al.teamf;

import fr.unice.polytech.al.teamf.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CarCrash {

    @Autowired
    NotifyUser notifyUser;
    
    public void notifyUser() {
        notifyUser.notifyUser(new User("Philippe"), "Hello World!");
    }
    
}
