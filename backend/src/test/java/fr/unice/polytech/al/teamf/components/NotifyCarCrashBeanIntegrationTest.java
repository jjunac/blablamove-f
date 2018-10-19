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

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ExtendWith(SpringExtension.class)
@Import({NotifyCarCrashBean.class, UserNotifierBean.class, FindDriverBean.class})
class NotifyCarCrashBeanIntegrationTest {

    @Autowired
    private NotifyCarCrashBean carCrash;

    @Test
    void shouldNotifyOwnersWhenADriverHasACarCrash() {
        // Mock output to make assert afterwards
        UserNotifierBean userNotifierBeanWithMockedOutput = new UserNotifierBean();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        userNotifierBeanWithMockedOutput.printStream = new PrintStream(out);
        carCrash.notifyUser = userNotifierBeanWithMockedOutput;

        User benjamin = new User("Benjamin");
        User philippe = new User("Philippe");
        User sebastien = new User("Sebastien");
        Parcel p1 = new Parcel(philippe);
        Parcel p2 = new Parcel(sebastien);
        benjamin.setTransportedPackages(Arrays.asList(p1, p2));
        carCrash.notifyCrash(benjamin);

        String[] outputLines = out.toString().split("\\r?\\n");
        System.out.println(Arrays.toString(outputLines));
        assertThat(outputLines[0]).contains("Philippe").contains(NotifyCarCrashBean.buildMessage("Benjamin"));
        assertThat(outputLines[1]).contains("Sebastien").contains(NotifyCarCrashBean.buildMessage("Benjamin"));
    }
}