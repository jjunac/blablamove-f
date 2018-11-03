package fr.unice.polytech.al.teamf.components;

import fr.unice.polytech.al.teamf.IntegrationTest;
import fr.unice.polytech.al.teamf.entities.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

@DataJpaTest
@ExtendWith(SpringExtension.class)
@Import(UserNotifierBean.class)
public class UserNotifierBeanIntegrationTest extends IntegrationTest {

    @Autowired
    private  UserNotifierBean userNotifierBean;

    @Test
    void shouldNotifyUser() {
        User jyd = createAndSaveUser("Jean-Yves (Delmotte)");
        userNotifierBean.notifyUser(jyd, "This is a test message");
        assertThat(userNotifierBean.pullNotificationForUser(jyd)).extracting("message").contains("This is a test message");
    }
}