package fr.unice.polytech.al.teamf.components;

import com.github.tomakehurst.wiremock.matching.StringValuePattern;
import fr.unice.polytech.al.teamf.IntegrationTest;
import fr.unice.polytech.al.teamf.PullNotifications;
import fr.unice.polytech.al.teamf.TestConfig;
import fr.unice.polytech.al.teamf.entities.GPSCoordinate;
import fr.unice.polytech.al.teamf.entities.Mission;
import fr.unice.polytech.al.teamf.entities.Notification;
import fr.unice.polytech.al.teamf.entities.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
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
@Import({CarCrashBean.class, UserNotifierBean.class, DriverFinderBean.class, TemporaryLocationBean.class, AccountingBean.class, TestConfig.class})
@AutoConfigureWireMock(port = 5000)
class CarCrashBeanIntegrationTest extends IntegrationTest {
    
    @Autowired
    private CarCrashBean carCrash;
    
    @Autowired
    private DriverFinderBean driverFinderBean;
    
    @Autowired
    private PullNotifications pullNotifications;
    
    @BeforeEach
    void setUp() {

        carCrash.accountingBean.rabbitTemplate = TestUtils.queueAndExchangeSetup(new AnnotationConfigApplicationContext(TestConfig.class),
                "point-pricing",
                "point-pricing-exchange",
                "pointpricing.*");

        carCrash.rabbitTemplate = TestUtils.queueAndExchangeSetup(new AnnotationConfigApplicationContext(TestConfig.class),
                "insurance",
                "insurance-exchange",
                "insurance.*");

        driverFinderBean.routeFinderUrl = "http://localhost:5000";
    
        Map<String, StringValuePattern> params = new HashMap<>();
        StringValuePattern number = matching("[+-]?([0-9]*[.])?[0-9]+");
        params.put("start_lat", number);
        params.put("start_long", number);
        params.put("end_lat", number);
        params.put("end_long", number);
        stubFor(get(urlPathEqualTo("/find_driver")).withQueryParams(params).willReturn(aResponse()
                .withBody("{\"drivers\":[{\"name\":\"Erick\"}]}").withStatus(200)));
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
                .contains(CarCrashBean.buildMessage("Benjamin"));

        assertThat(pullNotifications.pullNotificationForUser(sebastien))
                .asList()
                .extracting("message")
                .hasSize(1)
                .contains(CarCrashBean.buildMessage("Benjamin"));

        List<Notification> notifications = pullNotifications.pullNotificationForUser(erick);
        assertThat(notifications)
                .asList()
                .extracting("message")
                .hasSize(2)
                .contains(DriverFinderBean.buildNewDriverMessage("Benjamin", "Philippe"))
                .contains(DriverFinderBean.buildNewDriverMessage("Benjamin", "Sebastien"));

        Mission mission1 = missionRepository.findById((Long) notifications.get(0).getAnswer().getParameters().get("missionId")).get();
        driverFinderBean.answerToPendingMission(mission1, erick, true);
        Mission mission2 = missionRepository.findById((Long) notifications.get(1).getAnswer().getParameters().get("missionId")).get();
        driverFinderBean.answerToPendingMission(mission2, erick, true);

        assertThat(pullNotifications.pullNotificationForUser(philippe))
                .asList()
                .extracting("message")
                .hasSize(1)
                .contains(DriverFinderBean.buildOwnerMessage("Erick"));

        assertThat(pullNotifications.pullNotificationForUser(sebastien))
                .asList()
                .extracting("message")
                .hasSize(1)
                .contains(DriverFinderBean.buildOwnerMessage("Erick"));

        assertThat(pullNotifications.pullNotificationForUser(benjamin))
                .asList()
                .extracting("message")
                .hasSize(2)
                .contains(DriverFinderBean.buildCurrentDriverMessage("Erick", "Philippe"))
                .contains(DriverFinderBean.buildCurrentDriverMessage("Erick", "Sebastien"));

    }
}