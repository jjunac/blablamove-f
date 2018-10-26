package fr.unice.polytech.al.teamf.components;

import fr.unice.polytech.al.teamf.PullNotifications;
import fr.unice.polytech.al.teamf.entities.Parcel;
import fr.unice.polytech.al.teamf.entities.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ExtendWith(SpringExtension.class)
@Import({FindPackageHostBean.class, UserNotifierBean.class})
class FindHostBeanIntegrationTest {

    @Autowired
    private FindPackageHostBean hostFinder;

    @Autowired
    private PullNotifications pullNotifications;

    @Test
    void shouldNotifyOwnersWhenANewDriverHasBeenFound() {
        User paulette = new User("Paulette");
        hostFinder.findHost(new Parcel(paulette));

        assertThat(pullNotifications.pullNotificationForUser("Paulette"))
                .asList()
                .hasSize(1)
                .contains(FindPackageHostBean.buildOwnerMessage("Camille"));
        assertThat(pullNotifications.pullNotificationForUser("Camille"))
                .asList()
                .hasSize(1)
                .contains(FindPackageHostBean.buildHostMessage("Paulette"));
    }
}