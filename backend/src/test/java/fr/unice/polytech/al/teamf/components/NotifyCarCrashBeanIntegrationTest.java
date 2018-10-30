package fr.unice.polytech.al.teamf.components;

import fr.unice.polytech.al.teamf.PullNotifications;
import fr.unice.polytech.al.teamf.entities.Parcel;
import fr.unice.polytech.al.teamf.entities.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ExtendWith(SpringExtension.class)
@Import({NotifyCarCrashBean.class, UserNotifierBean.class, FindDriverBean.class})
class NotifyCarCrashBeanIntegrationTest {

    @Autowired
    private NotifyCarCrashBean carCrash;

    @Autowired
    private PullNotifications pullNotifications;

    @Test
    void shouldNotifyOwnersWhenADriverHasACarCrash() {

        User benjamin = new User("Benjamin");
        User philippe = new User("Philippe");
        User sebastien = new User("Sebastien");
        User erick = new User("Erick");
        Parcel p1 = new Parcel(philippe);
        Parcel p2 = new Parcel(sebastien);
        benjamin.setTransportedPackages(Arrays.asList(p1, p2));
        carCrash.notifyCrash(benjamin);

        assertThat(pullNotifications.pullNotificationForUser(philippe))
                .asList()
                .hasSize(2)
                .contains(NotifyCarCrashBean.buildMessage("Benjamin"))
                .contains(FindDriverBean.buildOwnerMessage("Erick"));

        assertThat(pullNotifications.pullNotificationForUser(sebastien))
                .asList()
                .hasSize(2)
                .contains(NotifyCarCrashBean.buildMessage("Benjamin"))
                .contains(FindDriverBean.buildOwnerMessage("Erick"));

        assertThat(pullNotifications.pullNotificationForUser(benjamin))
                .asList()
                .hasSize(2)
                .contains(FindDriverBean.buildCurrentDriverMessage("Erick", "Philippe"))
                .contains(FindDriverBean.buildCurrentDriverMessage("Erick", "Sebastien"));
        assertThat(pullNotifications.pullNotificationForUser(erick))
                .asList()
                .hasSize(2)
                .contains(FindDriverBean.buildNewDriverMessage("Philippe", "Benjamin"))
                .contains(FindDriverBean.buildNewDriverMessage("Sebastien", "Benjamin"));
    }
}