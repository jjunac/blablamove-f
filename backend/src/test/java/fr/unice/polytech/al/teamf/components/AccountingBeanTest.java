package fr.unice.polytech.al.teamf.components;

import fr.unice.polytech.al.teamf.Application;
import fr.unice.polytech.al.teamf.IntegrationTest;
import fr.unice.polytech.al.teamf.TestConfig;
import fr.unice.polytech.al.teamf.entities.GPSCoordinate;
import fr.unice.polytech.al.teamf.entities.Mission;
import fr.unice.polytech.al.teamf.entities.Parcel;
import fr.unice.polytech.al.teamf.entities.User;
import fr.unice.polytech.al.teamf.exceptions.UnknownUserException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestTemplate;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@Import({AccountingBean.class, RestTemplate.class, TestConfig.class, MessageReceiver.class, Application.class})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(port = 5001)
@Disabled
class AccountingBeanTest extends IntegrationTest {

    @Autowired
    private AccountingBean accountingBean;

    @BeforeEach
    public void setUp() {
        /* Returns the value after modification */
        stubFor(put(urlPathEqualTo("/users/Erick")).willReturn(aResponse()
                .withBody("20").withStatus(201)));
        stubFor(put(urlPathEqualTo("/users/Julien")).willReturn(aResponse()
                .withStatus(404)));
        accountingBean.point_pricing_url = "http://localhost:5001";
        accountingBean.rabbitTemplate = queueAndExchangeSetup(new AnnotationConfigApplicationContext(TestConfig.class),
                "point-pricing",
                "point-pricing-exchange",
                "pointpricing.*");
    }

    @Test
    void shouldChangeTheNumberOfPointsOfTheUser() throws UnknownUserException {
        /* We suppose Erick has 10 points before the test */
        User transporter = new User("Erick");
        User owner = new User("Thomas");
        GPSCoordinate transporterCoordinate = new GPSCoordinate(0, 10);
        GPSCoordinate ownerCoordinate = new GPSCoordinate(0, 20);
        Parcel parcel = new Parcel();
        Mission mission = new Mission(transporter, owner, transporterCoordinate, ownerCoordinate, parcel);
        assertEquals(20, accountingBean.computePoints(mission));
    }

    @Test
    void shouldNotChangeTheNumberOfPointsOfTheUser() throws UnknownUserException {
        /* User Julien does not exist in the database */
        User transporter = new User("Julien");
        User owner = new User("Thomas");
        GPSCoordinate transporterCoordinate = new GPSCoordinate(10, 20);
        GPSCoordinate ownerCoordinate = new GPSCoordinate(40, 50);
        Parcel parcel = new Parcel();
        Mission mission = new Mission(transporter, owner, transporterCoordinate, ownerCoordinate, parcel);
        assertEquals(0, accountingBean.computePoints(mission));
    }


}