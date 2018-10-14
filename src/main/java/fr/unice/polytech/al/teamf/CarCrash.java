package fr.unice.polytech.al.teamf;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

@Component
public class CarCrash {

    @Autowired
    NotifyUser notifyUser;
    
    public void notifyUser() {
        notifyUser.notifyUser(new User("Philippe"), "Hello World!");
    }
    
}
