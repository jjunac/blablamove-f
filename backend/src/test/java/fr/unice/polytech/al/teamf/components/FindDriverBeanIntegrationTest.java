package fr.unice.polytech.al.teamf.components;

import fr.unice.polytech.al.teamf.IntegrationTest;
import fr.unice.polytech.al.teamf.PullNotifications;
import fr.unice.polytech.al.teamf.entities.GPSCoordinate;
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
@Import({FindDriverBean.class, UserNotifierBean.class})
class FindDriverBeanIntegrationTest extends IntegrationTest {

    @Autowired
    private FindDriverBean driverFinder;
    @Autowired
    private PullNotifications pullNotifications;

    @Test
    void shouldNotifyOwnersWhenANewDriverHasBeenFound() {

        // We don't care about coordinates here
        GPSCoordinate gps = new GPSCoordinate(10, 20);

        User philippe = createAndSaveUser("Philippe");
        User benjamin = createAndSaveUser("Benjamin");
        // Get the mocked new transporter
        User erick = userRepository.findByName("Erick").get(0);
        driverFinder.findNewDriver(benjamin, createAndSaveMissionWithParcel(philippe, benjamin, gps, gps), gps);

        assertThat(pullNotifications.pullNotificationForUser(philippe))
                .asList()
                .extracting("message")
                .hasSize(1)
                .contains(FindDriverBean.buildOwnerMessage("Erick"));
        assertThat(pullNotifications.pullNotificationForUser(benjamin))
                .asList()
                .extracting("message")
                .hasSize(1)
                .contains(FindDriverBean.buildCurrentDriverMessage("Erick", "Philippe"));
        assertThat(pullNotifications.pullNotificationForUser(erick))
                .asList()
                .extracting("message")
                .hasSize(1)
                .contains(FindDriverBean.buildNewDriverMessage("Benjamin", "Philippe"));
    }
}