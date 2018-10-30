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

    @Autowired
    ComputePoints computePoints;

    @Override
    public boolean computePoints(Mission mission) {
        logger.trace("PackageDroppedImpl.computePoints");
        try {
            computePoints.computePoints(mission);
        } catch (UnknownUserException e) {
            logger.error(e.getMessage());
        }
        return false;
    }
}
