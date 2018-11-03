package fr.unice.polytech.al.teamf.components;

import fr.unice.polytech.al.teamf.IntegrationTest;
import fr.unice.polytech.al.teamf.PullNotifications;
import fr.unice.polytech.al.teamf.entities.GPSCoordinate;
import fr.unice.polytech.al.teamf.entities.Mission;
import fr.unice.polytech.al.teamf.entities.Parcel;
import fr.unice.polytech.al.teamf.entities.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
@ExtendWith(SpringExtension.class)
@Import({NotifyCarCrashBean.class, UserNotifierBean.class, FindDriverBean.class})
class NotifyCarCrashBeanIntegrationTest extends IntegrationTest {

    @Autowired
    private NotifyCarCrashBean carCrash;

    @Autowired
    private PullNotifications pullNotifications;

    @Test
    void shouldNotifyOwnersWhenADriverHasACarCrash() {

        // We don't care about coordinates here
        GPSCoordinate gps = new GPSCoordinate(10, 20);

        User benjamin = createAndSaveUser("Benjamin");
        User philippe = createAndSaveUser("Philippe");
        User sebastien = createAndSaveUser("Sebastien");
        User erick = userRepository.findByName("Erick").get(0);
        Mission m1 = createAndSaveMissionWithParcel(philippe, benjamin, gps, gps);
        Mission m2 = createAndSaveMissionWithParcel(sebastien, benjamin, gps, gps);

        carCrash.notifyCrash(benjamin, gps);

        assertThat(pullNotifications.pullNotificationForUser(philippe))
                .asList()
                .extracting("message")
                .hasSize(2)
                .contains(NotifyCarCrashBean.buildMessage("Benjamin"))
                .contains(FindDriverBean.buildOwnerMessage("Erick"));

        assertThat(pullNotifications.pullNotificationForUser(sebastien))
                .asList()
                .extracting("message")
                .hasSize(2)
                .contains(NotifyCarCrashBean.buildMessage("Benjamin"))
                .contains(FindDriverBean.buildOwnerMessage("Erick"));

        assertThat(pullNotifications.pullNotificationForUser(benjamin))
                .asList()
                .extracting("message")
                .hasSize(2)
                .contains(FindDriverBean.buildCurrentDriverMessage("Erick", "Philippe"))
                .contains(FindDriverBean.buildCurrentDriverMessage("Erick", "Sebastien"));
        assertThat(pullNotifications.pullNotificationForUser(erick))
                .asList()
                .extracting("message")
                .hasSize(2)
                .contains(FindDriverBean.buildNewDriverMessage("Benjamin", "Philippe"))
                .contains(FindDriverBean.buildNewDriverMessage("Benjamin", "Sebastien"));
    }
}