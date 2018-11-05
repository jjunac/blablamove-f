package fr.unice.polytech.al.teamf.components;

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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@Import({NotifyCarCrashBean.class, RestTemplate.class})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(port = 5000)
class NotifyCarCrashBeanTest {

    @Autowired
    private NotifyCarCrashBean carCrash;

    @BeforeEach
    public void setUp() {

        stubFor(get(urlPathEqualTo("/insurance/Jeremy")).willReturn(aResponse()
                .withBody("{\n \"insuranceInvolvement\": true\n}").withStatus(200)));
        stubFor(get(urlPathEqualTo("/insurance/Johann")).willReturn(aResponse()
                .withBody("{\n \"insuranceInvolvement\": false\n}").withStatus(200)));
        carCrash.insurance_url = "http://localhost:5000";
    }

    @Test
    void shouldBeCoveredByInsurance() throws UnknownUserException {
        assertTrue(carCrash.contactInsurance(new User("Jeremy")));
    }

    @Test
    void shouldNotChangeTheNumberOfPointsOfTheUser() throws UnknownUserException {
        assertFalse(carCrash.contactInsurance(new User("Johann")));
    }


}