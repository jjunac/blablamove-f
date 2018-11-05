package fr.unice.polytech.al.teamf.webservices;

import com.googlecode.jsonrpc4j.spring.AutoJsonRpcServiceImpl;
import fr.unice.polytech.al.teamf.ComputePoints;
import fr.unice.polytech.al.teamf.entities.Mission;
import fr.unice.polytech.al.teamf.exceptions.UnknownUserException;
import fr.unice.polytech.al.teamf.repositories.MissionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AutoJsonRpcServiceImpl
@Slf4j
public class PackageDroppedImpl implements PackageDropped {

    @Autowired
    MissionRepository missionRepository;

    @Autowired
    ComputePoints computePoints;

    @Override
    public boolean missionFinished(long missionId) {
        log.trace("PackageDroppedImpl.missionFinished");
        Optional <Mission> mission = missionRepository.findById(missionId);
        if (mission.isPresent()) {
            try {
                computePoints.computePoints(mission.get());
                mission.get().setFinished(); // useless for the moment, but may be useful if we want to keep a history
                mission.get().getTransporter().removeTransportedMission(mission.get());
                mission.get().getOwner().removeOwnedMission(mission.get());
                missionRepository.delete(mission.get());
                return true;
            } catch (UnknownUserException e) {
                log.error(e.getMessage());
            }
        }
        return false;
    }
}
