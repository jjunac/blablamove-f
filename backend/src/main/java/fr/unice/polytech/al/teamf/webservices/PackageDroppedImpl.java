package fr.unice.polytech.al.teamf.webservices;

import com.googlecode.jsonrpc4j.spring.AutoJsonRpcServiceImpl;
import fr.unice.polytech.al.teamf.ComputePoints;
import fr.unice.polytech.al.teamf.entities.Mission;
import fr.unice.polytech.al.teamf.exceptions.UnknownUserException;
import fr.unice.polytech.al.teamf.repositories.MissionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AutoJsonRpcServiceImpl
public class PackageDroppedImpl implements PackageDropped {

    private final Logger logger = LoggerFactory.getLogger(PackageDroppedImpl.class);

    @Autowired
    MissionRepository missionRepository;

    @Autowired
    ComputePoints computePoints;

    @Override
    public boolean computePoints(long missionId) {
        logger.trace("PackageDroppedImpl.computePoints");
        Optional <Mission> mission = missionRepository.findById(missionId);
        if (mission.isPresent()) {
            try {
                computePoints.computePoints(mission.get());
                return true;
            } catch (UnknownUserException e) {
                logger.error(e.getMessage());
            }
        }
        return false;
    }
}
