package fr.unice.polytech.al.teamf.components;

import fr.unice.polytech.al.teamf.entities.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

@ExtendWith(SpringExtension.class)
@Import(UserNotifierBean.class)
public class UserNotifierBeanTest {

    @Autowired
    private  UserNotifierBean userNotifierBean;

    @Test
    void shouldNotifyUser() {
        userNotifierBean.notifyUser(new User("Jean-Yves (Delmotte)"), "This is a test message");
        assertThat(userNotifierBean.pullNotificationForUser("Jean-Yves (Delmotte)")).contains("This is a test message");
    }
}