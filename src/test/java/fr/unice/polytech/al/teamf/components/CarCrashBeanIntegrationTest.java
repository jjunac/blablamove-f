package fr.unice.polytech.al.teamf.components;

import fr.unice.polytech.al.teamf.entities.Parcel;
import fr.unice.polytech.al.teamf.entities.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@Import({CarCrashBean.class, UserNotifierBean.class})
class CarCrashBeanIntegrationTest {

    @Autowired
    private CarCrashBean carCrash;

    @Test
    void notifyUser() {
        User benjamin = new User("Benjamin");
        User philippe = new User("Philippe");
        User sebastien = new User("Sebastien");
        Parcel p1 = new Parcel(philippe);
        Parcel p2 = new Parcel(sebastien);
        benjamin.setTransportedPackages(Arrays.asList(p1, p2));
        assertEquals(2, benjamin.getTransportedPackages().size());
        assertEquals(2, benjamin.getUsersOwningPackages().size());
        carCrash.notifyCrash(benjamin);
    }
}