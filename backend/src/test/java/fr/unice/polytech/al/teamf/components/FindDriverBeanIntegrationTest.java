package fr.unice.polytech.al.teamf.components;

import fr.unice.polytech.al.teamf.PullNotifications;
import fr.unice.polytech.al.teamf.entities.Parcel;
import fr.unice.polytech.al.teamf.entities.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ExtendWith(SpringExtension.class)
@Import({FindDriverBean.class, UserNotifierBean.class})
class FindDriverBeanIntegrationTest {

    @Autowired
    private FindDriverBean driverFinder;

    @Autowired
    private PullNotifications pullNotifications;

    @Test
    void shouldNotifyOwnersWhenANewDriverHasBeenFound() {
        User philippe = new User("Philippe");
        User benjamin = new User("Benjamin");
        driverFinder.findNewDriver(benjamin, new Parcel(philippe));

        assertThat(pullNotifications.pullNotificationForUser("Philippe"))
                .asList()
                .hasSize(1)
                .contains(FindDriverBean.buildOwnerMessage("Erick"));
        assertThat(pullNotifications.pullNotificationForUser("Benjamin"))
                .asList()
                .hasSize(1)
                .contains(FindDriverBean.buildDriverMessage("Erick", "Philippe"));
    }
}