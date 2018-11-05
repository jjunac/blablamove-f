package fr.unice.polytech.al.teamf.components;

import com.github.tomakehurst.wiremock.matching.StringValuePattern;
import fr.unice.polytech.al.teamf.IntegrationTest;
import fr.unice.polytech.al.teamf.PullNotifications;
import fr.unice.polytech.al.teamf.entities.GPSCoordinate;
import fr.unice.polytech.al.teamf.entities.Mission;
import fr.unice.polytech.al.teamf.entities.Notification;
import fr.unice.polytech.al.teamf.entities.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
@ExtendWith(SpringExtension.class)
@Import({NotifyCarCrashBean.class, UserNotifierBean.class, FindDriverBean.class, FindPackageHostBean.class})
@AutoConfigureWireMock(port = 5000)
class NotifyCarCrashBeanIntegrationTest extends IntegrationTest {
    
    @Autowired
    private NotifyCarCrashBean carCrash;
    
    @Autowired
    private FindDriverBean driverFinder;
    
    @Autowired
    private PullNotifications pullNotifications;
    
    @BeforeEach
    void setUp() {
        IntegrationTest.setupDriverFinder(driverFinder);
    }
    
    @Test
    void shouldNotifyOwnersWhenADriverHasACarCrash() {
        
        // We don't care about coordinates here
        GPSCoordinate gps = new GPSCoordinate(10, 20);
        
        User benjamin = createAndSaveUser("Benjamin");
        User philippe = createAndSaveUser("Philippe");
        User sebastien = createAndSaveUser("Sebastien");
        User erick = userRepository.findByName("Erick").get(0);
        Mission m1 = createAndSaveOngoingdMissionWithParcel(philippe, benjamin, gps, gps);
        Mission m2 = createAndSaveOngoingdMissionWithParcel(sebastien, benjamin, gps, gps);
        
        carCrash.notifyCrash(benjamin, gps);

        assertThat(pullNotifications.pullNotificationForUser(philippe))
                .asList()
                .extracting("message")
                .hasSize(1)
                .contains(NotifyCarCrashBean.buildMessage("Benjamin"));

        assertThat(pullNotifications.pullNotificationForUser(sebastien))
                .asList()
                .extracting("message")
                .hasSize(1)
                .contains(NotifyCarCrashBean.buildMessage("Benjamin"));

        List<Notification> notifications = pullNotifications.pullNotificationForUser(erick);
        assertThat(notifications)
                .asList()
                .extracting("message")
                .hasSize(2)
                .contains(FindDriverBean.buildNewDriverMessage("Benjamin", "Philippe"))
                .contains(FindDriverBean.buildNewDriverMessage("Benjamin", "Sebastien"));

        Mission mission1 = missionRepository.findById((Long) notifications.get(0).getAnswer().getParameters().get("missionId")).get();
        driverFinder.answerToPendingMission(mission1, erick, true);
        Mission mission2 = missionRepository.findById((Long) notifications.get(1).getAnswer().getParameters().get("missionId")).get();
        driverFinder.answerToPendingMission(mission2, erick, true);

        assertThat(pullNotifications.pullNotificationForUser(philippe))
                .asList()
                .extracting("message")
                .hasSize(1)
                .contains(FindDriverBean.buildOwnerMessage("Erick"));

        assertThat(pullNotifications.pullNotificationForUser(sebastien))
                .asList()
                .extracting("message")
                .hasSize(1)
                .contains(FindDriverBean.buildOwnerMessage("Erick"));

        assertThat(pullNotifications.pullNotificationForUser(benjamin))
                .asList()
                .extracting("message")
                .hasSize(2)
                .contains(FindDriverBean.buildCurrentDriverMessage("Erick", "Philippe"))
                .contains(FindDriverBean.buildCurrentDriverMessage("Erick", "Sebastien"));

    }
}