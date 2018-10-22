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
        // Mocking output
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        userNotifierBean.printStream = new PrintStream(out);

        userNotifierBean.notifyUser(new User("Clara"), "This is a test message");
        assertThat(out.toString()).contains("Clara").contains("This is a test message");
    }
}