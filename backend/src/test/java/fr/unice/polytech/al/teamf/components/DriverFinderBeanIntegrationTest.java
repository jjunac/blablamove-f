package fr.unice.polytech.al.teamf.components;

import com.github.tomakehurst.wiremock.matching.StringValuePattern;
import fr.unice.polytech.al.teamf.IntegrationTest;
import fr.unice.polytech.al.teamf.TestConfig;
import fr.unice.polytech.al.teamf.entities.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.matching;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
@ExtendWith(SpringExtension.class)
@Import({DriverFinderBean.class, TemporaryLocationBean.class, PackageBean.class, AccountingBean.class, TestConfig.class})
@AutoConfigureWireMock(port = 5008)
class DriverFinderBeanIntegrationTest extends IntegrationTest {

    @Autowired
    private DriverFinderBean driverFinder;
    @Autowired
    private PackageBean managePackage;


    @BeforeAll
    void setUpAll() {
        super.setUp();
        driverFinder.rabbitTemplate = TestUtils.queueAndExchangeSetup(new AnnotationConfigApplicationContext(TestConfig.class),
                "route-finder",
                "route-finder-exchange",
                "routefinder.*");

        Map<String, StringValuePattern> params = new HashMap<>();
        StringValuePattern number = matching("[+-]?([0-9]*[.])?[0-9]+");
        params.put("start_lat", number);
        params.put("start_long", number);
        params.put("end_lat", number);
        params.put("end_long", number);
    }
    
//TODO Create new tests


//    @Test
//    void shouldNotifyOwnersWhenANewDriverHasBeenFound() {
//
//        // We don't care about coordinates here
//        GPSCoordinate gps = new GPSCoordinate(10, 20);
//
//        User philippe = createAndSaveUser("Philippe");
//        User benjamin = createAndSaveUser("Benjamin");
//        // Get the mocked new transporter
//        Parcel parcel = createAndSaveParcel(philippe);
//        parcel.setKeeper(benjamin);
//        User newDriver = driverFinder.findNewDriver(benjamin, parcel, gps, gps);
//        List<Notification> notifications = pullNotifications.pullNotificationForUser(newDriver);
//        assertThat(notifications)
//                .asList()
//                .extracting("message")
//                .hasSize(1)
//                .contains(DriverFinderBean.buildNewDriverMessage("Benjamin", "Philippe"));
//        Mission mission = missionRepository.findById((Long) notifications.get(0).getAnswer().getParameters().get("missionId")).get();
//        driverFinder.answerToPendingMission(mission, newDriver, true);
//        assertThat(pullNotifications.pullNotificationForUser(philippe))
//                .asList()
//                .extracting("message")
//                .hasSize(1)
//                .contains(DriverFinderBean.buildOwnerMessage("Erick"));
//        assertThat(pullNotifications.pullNotificationForUser(benjamin))
//                .asList()
//                .extracting("message")
//                .hasSize(1)
//                .contains(DriverFinderBean.buildCurrentDriverMessage("Erick", "Philippe"));
//
//    }
//
//    @Test
//    void shouldNotifyOwnersWhenTheNewDriverTakeThePackage() {
//
//        // We don't care about coordinates here
//        GPSCoordinate gps = new GPSCoordinate(10, 20);
//
//        User owner = createAndSaveUser("Philippe");
//        // Get the mocked new transporter
//        User erick = userRepository.findByName("Erick").get(0);
//        Parcel parcel = createAndSaveParcel(owner);
//        Mission mission = new Mission(erick, owner, gps, gps, parcel);
//        managePackage.takePackageFromDriver(erick, mission);
//
//        assertThat(pullNotifications.pullNotificationForUser(owner))
//                .asList()
//                .extracting("message")
//                .hasSize(1)
//                .contains(PackageBean.buildChangeDriverMessage("Erick"));
//    }
}