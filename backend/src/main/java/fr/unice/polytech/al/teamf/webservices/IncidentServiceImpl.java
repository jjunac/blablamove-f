package fr.unice.polytech.al.teamf.webservices;

import com.googlecode.jsonrpc4j.spring.AutoJsonRpcServiceImpl;
import fr.unice.polytech.al.teamf.NotifyCarCrash;
import fr.unice.polytech.al.teamf.entities.GPSCoordinate;
import fr.unice.polytech.al.teamf.entities.Mission;
import fr.unice.polytech.al.teamf.entities.Parcel;
import fr.unice.polytech.al.teamf.entities.User;
import fr.unice.polytech.al.teamf.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@AutoJsonRpcServiceImpl
public class IncidentServiceImpl implements IncidentService {

    private final Logger logger = LoggerFactory.getLogger(IncidentServiceImpl.class);

    @Autowired
    UserRepository userRepository;

    Map<String, User> users = new HashMap<>();
    {
        User thomas = new User("Thomas");
        users.put("Thomas", thomas);
        User loic = new User("Loic");
        users.put("Loic", loic);
        User jeremy = new User("Jeremy");
        users.put("Jeremy", jeremy);
        User johann = new User("Johann");
        johann.addTransportedMission(new Mission(johann, jeremy, new Parcel()));
        johann.addTransportedMission(new Mission(johann, thomas, new Parcel()));
        users.put("Johann", johann);
    }

    @Autowired
    NotifyCarCrash notifyCarCrash;

    @Override
    public boolean notifyCarCrash(String username, double latitude, double longitude) {
        logger.trace("IncidentServiceImpl.notifyCarCrash");
        logger.debug(userRepository.findAll().toString());
        // For the POC, assume the existence and the unicity
        User user = userRepository.findByName(username).get(0);
        logger.debug(user.toString());
        notifyCarCrash.notifyCrash(user, new GPSCoordinate(latitude, longitude));
        return true;
    }
}
