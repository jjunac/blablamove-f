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

import static junit.framework.TestCase.assertNull;
import static junit.framework.TestCase.assertTrue;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.Assert.assertEquals;

@DataJpaTest
@ExtendWith(SpringExtension.class)
@Import({TemporaryLocationBean.class, UserNotifierBean.class, PackageBean.class})
class FindHostBeanIntegrationTest extends IntegrationTest {

    @Autowired
    private TemporaryLocationBean hostFinder;
    @Autowired
    private PackageBean managePackage;
    @Autowired
    private PullNotifications pullNotifications;

    @Test
    void shouldNotifyOwnersWhenANewDriverHasBeenFound() {
        User paulette = createAndSaveUser("Paulette");
        User julien = userRepository.findByName("Julien").get(0);
        hostFinder.findHost(createAndSaveParcel(paulette));

        // FIXME test that the current transporter is notified


        assertThat(pullNotifications.pullNotificationForUser(julien))
                .asList()
                .extracting("message")
                .hasSize(1)
                .contains(TemporaryLocationBean.buildHostMessage("Paulette"));
    }

    @Test
    void shouldNotifyOwnerWhenThePackageIsDroppedToTemporaryLocation() {
        // We don't care about coordinates here
        GPSCoordinate gps = new GPSCoordinate(10, 20);
        User paulette = createAndSaveUser("Paulette");
        User georgette = createAndSaveUser("Georgette");
        User julien = userRepository.findByName("Julien").get(0);
        Parcel parcel = createAndSaveParcel(georgette);
        Mission mission = new Mission(paulette, georgette, gps, gps, parcel);
        managePackage.dropPackageToHost(julien, mission);

        assertNull(mission.getTransporter());
        assertEquals(julien, mission.getParcel().getKeeper());
        assertThat(pullNotifications.pullNotificationForUser(georgette))
                .asList()
                .extracting("message")
                .hasSize(1)
                .contains(PackageBean.buildDroppedPackageMessage("Julien"));
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
        assertThat(pullNotifications.pullNotificationForUser(georgette))
                .asList()
                .extracting("message")
                .hasSize(1)
                .contains(PackageBean.buildTakenPackageMessage("Paulette"));
    }
}