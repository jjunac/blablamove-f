package fr.unice.polytech.al.teamf.components;

import fr.unice.polytech.al.teamf.entities.Mission;
import fr.unice.polytech.al.teamf.entities.User;
import fr.unice.polytech.al.teamf.exceptions.UnknownUserException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestTemplate;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
@Import({AccountingBean.class, RestTemplate.class})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(port = 5000)
class AccountingBeanTest {

    @Autowired
    private AccountingBean accountingBean;

    @BeforeEach
    public void setUp() {
        stubFor(put(urlPathEqualTo("/users/Jerome")).willReturn(aResponse().withBody("30").withStatus(201)));
        stubFor(put(urlPathEqualTo("/users/Julien")).willReturn(aResponse().withStatus(404)));
    }

    @Test
    void shouldChangeTheNumberOfPointsOfTheUser() throws UnknownUserException {
        User user = new User("Jerome");
        Mission mission = new Mission(user, 20);
        assertEquals(30, accountingBean.computePoints(user, mission));
    }

    @Test
    void shouldNotChangeTheNumberOfPointsOfTheUser() {
        User user = new User("Julien");
        Mission mission = new Mission(user, 20);
        assertThrows(UnknownUserException.class, () -> accountingBean.computePoints(user, mission));
    }
}