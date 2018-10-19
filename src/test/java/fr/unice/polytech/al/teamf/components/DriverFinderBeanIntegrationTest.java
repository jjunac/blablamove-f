package fr.unice.polytech.al.teamf.components;

import fr.unice.polytech.al.teamf.entities.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Arrays;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ExtendWith(SpringExtension.class)
@Import({DriverFinderBean.class, UserNotifierBean.class})
class DriverFinderBeanIntegrationTest {

    @Autowired
    private DriverFinderBean driverFinder;

    @Test
    void shouldNotifyOwnersWhenANewDriverHasBeenFound() {
        // Mock output to make assert afterwards
        UserNotifierBean userNotifierBeanWithMockedOutput = new UserNotifierBean();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        userNotifierBeanWithMockedOutput.printStream = new PrintStream(out);
        driverFinder.notifyUser = userNotifierBeanWithMockedOutput;

        User philippe = new User("Philippe");
        driverFinder.findNewDriver(philippe);

        String[] outputLines = out.toString().split("\\r?\\n");
        System.out.println(Arrays.toString(outputLines));
        assertThat(outputLines[0]).contains("Philippe").contains(DriverFinderBean.buildMessage("Erick"));
    }
}