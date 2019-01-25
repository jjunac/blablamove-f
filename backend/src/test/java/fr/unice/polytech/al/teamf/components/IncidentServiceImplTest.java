package fr.unice.polytech.al.teamf.components;

import com.github.tomakehurst.wiremock.matching.StringValuePattern;
import fr.unice.polytech.al.teamf.IntegrationTest;
import fr.unice.polytech.al.teamf.PullNotifications;
import fr.unice.polytech.al.teamf.TestConfig;
import fr.unice.polytech.al.teamf.components.*;
import fr.unice.polytech.al.teamf.webservices.IncidentServiceImpl;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.HashMap;
import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.matching;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ExtendWith(SpringExtension.class)
@Import({IncidentServiceImpl.class, CarCrashBean.class, UserNotifierBean.class, DriverFinderBean.class, TemporaryLocationBean.class, AccountingBean.class, TestConfig.class})
@AutoConfigureWireMock(port = 5008)
class IncidentServiceImplTest extends IntegrationTest {

    @Autowired
    private CarCrashBean carCrash;

    @Autowired
    private DriverFinderBean driverFinderBean;

    @Autowired
    private PullNotifications pullNotifications;

    @Autowired
    private IncidentServiceImpl incidentService;

    @BeforeAll
    void setUpAll() {
        super.setUp();
        carCrash.accountingBean.rabbitTemplate = TestUtils.queueAndExchangeSetup(new AnnotationConfigApplicationContext(TestConfig.class),
                "point-pricing",
                "point-pricing-exchange",
                "pointpricing.*");

        carCrash.rabbitTemplate = TestUtils.queueAndExchangeSetup(new AnnotationConfigApplicationContext(TestConfig.class),
                "insurance",
                "insurance-exchange",
                "insurance.*");

        driverFinderBean.rabbitTemplate = TestUtils.queueAndExchangeSetup(new AnnotationConfigApplicationContext(TestConfig.class),
                "route-finder",
                "route-finder-exchange",
                "routefinder.*");

        Map<String, StringValuePattern> params = new HashMap<>();
        StringValuePattern number = matching("[+-]?([0-9]*[.])?[0-9]+");
        params.put("start_lat", number);
        params.put("start_long", number);
        params.put("end_lat", number);
        params.put("end_long", number);
    }

    @Test
    void shouldNotifyCarCrash() {
        assertTrue(incidentService.notifyCarCrash("Johann", 42, 42));
    }
}