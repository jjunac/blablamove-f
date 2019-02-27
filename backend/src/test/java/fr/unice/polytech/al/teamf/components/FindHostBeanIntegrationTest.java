package fr.unice.polytech.al.teamf.components;

import fr.unice.polytech.al.teamf.IntegrationTest;
import fr.unice.polytech.al.teamf.TestConfig;
import fr.unice.polytech.al.teamf.entities.GPSCoordinate;
import fr.unice.polytech.al.teamf.entities.Parcel;
import fr.unice.polytech.al.teamf.entities.User;
import org.junit.Ignore;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.Assert.assertEquals;

@DataJpaTest
@ExtendWith(SpringExtension.class)
@Import({TemporaryLocationBean.class, PackageBean.class, AccountingBean.class, TestConfig.class})
@AutoConfigureWireMock(port = 5008)
class FindHostBeanIntegrationTest extends IntegrationTest {

    @Autowired
    private TemporaryLocationBean hostFinder;
    @Autowired
    private PackageBean managePackage;


    @BeforeAll
    void setUpAll() {
        super.setUp();
        hostFinder.rabbitTemplate = TestUtils.queueAndExchangeSetup(new AnnotationConfigApplicationContext(TestConfig.class),
                "notifications",
                "notifications-exchange",
                "notifications.*");
    }
    

    @Test
    void shouldNotifyOwnerWhenThePackageIsDroppedToTemporaryLocation() {
        // We don't care about coordinates here
        GPSCoordinate gps = new GPSCoordinate(10, 20);
        User paulette = createAndSaveUser("Paulette");
        User georgette = createAndSaveUser("Georgette");
        User julien = userRepository.findByName("Julien").get(0);
        Parcel parcel = createAndSaveParcel(georgette);
        managePackage.dropPackageToHost(julien, parcel);

        assertEquals(julien, parcel.getKeeper());
    }

    @Test
    void shouldNotifyOwnerWhenThePackageIsTakenFromTemporaryLocation() {
        // We don't care about coordinates here
        GPSCoordinate gps = new GPSCoordinate(10, 20);
        User paulette = createAndSaveUser("Paulette");
        User georgette = createAndSaveUser("Georgette");
        Parcel parcel = createAndSaveParcel(georgette);
        int transportedMissionsAmount = paulette.getTransportedMissions().size();
        managePackage.takePackageFromHost(paulette, parcel);

        assertEquals(transportedMissionsAmount + 1, paulette.getTransportedMissions().size());
    }
}