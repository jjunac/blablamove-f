package fr.unice.polytech.al.teamf.webservices;

import com.googlecode.jsonrpc4j.spring.AutoJsonRpcServiceImpl;
import fr.unice.polytech.al.teamf.NotifyCarCrash;
import fr.unice.polytech.al.teamf.components.UserNotifierBean;
import fr.unice.polytech.al.teamf.entities.Parcel;
import fr.unice.polytech.al.teamf.entities.User;
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

    Map<String, User> users = new HashMap<>();
    {
        users.put("Thomas", new User("Thomas"));
        users.put("Loic", new User("Loic"));
        User jeremy = new User("Jeremy");
        users.put("Jeremy", jeremy);
        User johann = new User("Johann");
        johann.addTransportedPackage(new Parcel(jeremy));
        users.put("Johann", johann);
    }

    @Autowired
    NotifyCarCrash notifyCarCrash;

    @Override
    public boolean notifyCarCrash(String username) {
        logger.trace("IncidentServiceImpl.notifyCarCrash");
        notifyCarCrash.notifyCrash(users.get(username));
        return true;
    }
}
