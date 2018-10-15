package fr.unice.polytech.al.teamf;

import fr.unice.polytech.al.teamf.components.CarCrashBean;
import fr.unice.polytech.al.teamf.components.UserNotifierBean;
import fr.unice.polytech.al.teamf.entities.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@Import(UserNotifierBean.class)
class UserNotifierBeanTest {

    @Autowired
    private UserNotifierBean bean;

    @Test
    void shouldNotifyUser() {
        bean.notifyUser(new User("Clara"), "coucou");
    }
}