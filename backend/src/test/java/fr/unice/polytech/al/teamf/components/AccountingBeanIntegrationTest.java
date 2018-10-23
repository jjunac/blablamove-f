package fr.unice.polytech.al.teamf.components;

import fr.unice.polytech.al.teamf.entities.Mission;
import fr.unice.polytech.al.teamf.entities.User;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

@ExtendWith(SpringExtension.class)
@Import({AccountingBean.class, RestTemplate.class})
@Disabled
class AccountingBeanIntegrationTest {

    @Autowired
    private AccountingBean accountingBean;

    @Autowired
    private RestTemplate restTemplate;

    private MockRestServiceServer server;

    @Before
    public void setUp() {
        server = MockRestServiceServer.createServer(restTemplate);
        server.expect(requestTo("http://localhost:5000/users/Jerome"))
                .andExpect(method(HttpMethod.PUT))
                .andRespond(withSuccess());
        server.expect(requestTo("http://localhost:5000/users/Julien"))
                .andRespond(withStatus(HttpStatus.NOT_FOUND));
    }

    @Test
    void shouldChangeTheNumberOfPointsOfTheUser() {
        User user = new User("Jerome");
        user.setPoints(10);
        Mission mission = new Mission(user, 20);
        accountingBean.computePoints(user, mission);
        assertEquals(20, user.getPoints());
    }

    @Test
    void shouldNotChangeTheNumberOfPointsOfTheUser() {
        User user = new User("Julien");
        user.setPoints(10);
        Mission mission = new Mission(user, 20);
        accountingBean.computePoints(user, mission);
        assertEquals(10, user.getPoints());
    }
}