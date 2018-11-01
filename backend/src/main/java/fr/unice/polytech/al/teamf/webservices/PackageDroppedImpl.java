package fr.unice.polytech.al.teamf.webservices;

import com.googlecode.jsonrpc4j.spring.AutoJsonRpcServiceImpl;
import fr.unice.polytech.al.teamf.ComputePoints;
import fr.unice.polytech.al.teamf.NotifyCarCrash;
import fr.unice.polytech.al.teamf.entities.Mission;
import fr.unice.polytech.al.teamf.entities.Parcel;
import fr.unice.polytech.al.teamf.entities.User;
import fr.unice.polytech.al.teamf.exceptions.UnknownUserException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@AutoJsonRpcServiceImpl
public class PackageDroppedImpl implements PackageDropped {

    private final Logger logger = LoggerFactory.getLogger(PackageDroppedImpl.class);
    private final Map<Integer, Mission> missions = new HashMap<>();
    {
        missions.put(1, new Mission(new User("Erick"), 20));
    }

    @Autowired
    ComputePoints computePoints;

    @Override
    public boolean computePoints(int missionId) {
        logger.trace("PackageDroppedImpl.computePoints");
        try {
            Mission mission = missions.get(missionId);
            computePoints.computePoints(mission);
            return true;
        } catch (UnknownUserException e) {
            logger.error(e.getMessage());
        }
        return false;
    }
}
