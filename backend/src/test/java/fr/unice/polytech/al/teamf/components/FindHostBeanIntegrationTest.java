package fr.unice.polytech.al.teamf.components;

import fr.unice.polytech.al.teamf.IntegrationTest;
import fr.unice.polytech.al.teamf.PullNotifications;
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
@Import({FindPackageHostBean.class, UserNotifierBean.class})
class FindHostBeanIntegrationTest extends IntegrationTest {

    @Autowired
    private FindPackageHostBean hostFinder;

    @Autowired
    private PullNotifications pullNotifications;

    @Test
    void shouldNotifyOwnersWhenANewDriverHasBeenFound() {
        User paulette = createAndSaveUser("Paulette");
        User georgette = createAndSaveUser("Georgette");
        User julien = userRepository.findByName("Julien").get(0);
        hostFinder.findHost(createAndSaveParcel(paulette));

        // FIXME test that the current transporter is notified

        assertThat(pullNotifications.pullNotificationForUser(paulette))
                .asList()
                .extracting("message")
                .hasSize(1)
                .contains(FindPackageHostBean.buildOwnerMessage("Julien"));
        assertThat(pullNotifications.pullNotificationForUser(julien))
                .asList()
                .extracting("message")
                .hasSize(1)
                .contains(FindPackageHostBean.buildHostMessage("Paulette"));
    }
}