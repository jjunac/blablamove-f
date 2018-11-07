package fr.unice.polytech.al.teamf;

import com.github.tomakehurst.wiremock.matching.StringValuePattern;
import fr.unice.polytech.al.teamf.components.DriverFinderBean;
import fr.unice.polytech.al.teamf.entities.GPSCoordinate;
import fr.unice.polytech.al.teamf.entities.Mission;
import fr.unice.polytech.al.teamf.entities.Parcel;
import fr.unice.polytech.al.teamf.entities.User;
import fr.unice.polytech.al.teamf.repositories.MissionRepository;
import fr.unice.polytech.al.teamf.repositories.ParcelRepository;
import fr.unice.polytech.al.teamf.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

public abstract class IntegrationTest {
    
    @Autowired
    protected UserRepository userRepository;
    @Autowired
    protected ParcelRepository parcelRepository;
    @Autowired
    protected MissionRepository missionRepository;
    
    
    public static void setupDriverFinder(DriverFinderBean driverFinder) {
        driverFinder.setRouteFinderUrl("http://localhost:5000");
        
        Map<String, StringValuePattern> params = new HashMap<>();
        StringValuePattern number = matching("[+-]?([0-9]*[.])?[0-9]+");
        params.put("start_lat", number);
        params.put("start_long", number);
        params.put("end_lat", number);
        params.put("end_long", number);
        stubFor(get(urlPathEqualTo("/find_driver")).withQueryParams(params).willReturn(aResponse()
                .withBody("{\"drivers\":[{\"name\":\"Erick\"}]}").withStatus(200)));
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
