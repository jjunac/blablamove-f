package fr.unice.polytech.al.teamf;

import fr.unice.polytech.al.teamf.entities.GPSCoordinate;
import fr.unice.polytech.al.teamf.entities.Mission;
import fr.unice.polytech.al.teamf.entities.Parcel;
import fr.unice.polytech.al.teamf.entities.User;
import fr.unice.polytech.al.teamf.repositories.MissionRepository;
import fr.unice.polytech.al.teamf.repositories.ParcelRepository;
import fr.unice.polytech.al.teamf.repositories.UserRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestTemplate;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

@ExtendWith(SpringExtension.class)
@Import({TestConfig.class, RestTemplate.class})
@TestPropertySource("/external_services_test.properties")
@AutoConfigureWireMock(port = 5008)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class IntegrationTest {
    
    @Autowired
    protected UserRepository userRepository;
    @Autowired
    protected ParcelRepository parcelRepository;
    @Autowired
    protected MissionRepository missionRepository;
    @Autowired
    protected Application application;

    @BeforeAll
    protected void setUp(){
        stubFor(get(urlPathEqualTo("/settings")).willReturn(aResponse()
                .withBody("{\"notify_car_crash\":\"0.2\",\"notify_package_hosting\":\"0\"}").withStatus(200)));
    }

    public User createAndSaveUser(String name) {
        User user = new User(name);
        userRepository.save(user);
        return user;
    }
    
    public Parcel createAndSaveParcel(User owner) {
        Parcel parcel = new Parcel(owner);
        parcelRepository.save(parcel);
        return parcel;
    }
    
    public Mission createAndSaveOngoingdMissionWithParcel(User owner, User transporter, GPSCoordinate departure, GPSCoordinate arrival) {
        Parcel parcel = new Parcel(owner);
        Mission mission = new Mission(transporter, owner, departure, arrival, parcel);
        mission.setOngoing();
        parcel.setMission(mission);
        transporter.addTransportedMission(mission);
        parcelRepository.save(parcel);
        missionRepository.save(mission);
        return mission;
    }


    
}
