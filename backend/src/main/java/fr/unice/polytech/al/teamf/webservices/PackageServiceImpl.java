package fr.unice.polytech.al.teamf.webservices;

import com.googlecode.jsonrpc4j.spring.AutoJsonRpcServiceImpl;
import fr.unice.polytech.al.teamf.ComputePoints;
import fr.unice.polytech.al.teamf.FindDriver;
import fr.unice.polytech.al.teamf.FindPackageHost;
import fr.unice.polytech.al.teamf.entities.Mission;
import fr.unice.polytech.al.teamf.exceptions.UnknownUserException;
import fr.unice.polytech.al.teamf.repositories.MissionRepository;
import fr.unice.polytech.al.teamf.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@AutoJsonRpcServiceImpl
public class PackageServiceImpl implements PackageService {

    @Autowired
    MissionRepository missionRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    ComputePoints computePoints;
    @Autowired
    FindDriver findDriver;
    @Autowired
    FindPackageHost findPackageHost;


    @Override
    public boolean computePoints(long missionId) {
        log.trace("PackageDroppedImpl.computePoints");
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

    @Override
    public boolean answerToPendingMission(long missionId, String username, boolean answer) {
        log.trace("PackageServiceImpl.answerToPendingMission");
        return findDriver.answerToPendingMission(missionRepository.findById(missionId).get(), userRepository.findByName(username).get(0), answer);
    }

    @Override
    public void takePackage(long missionId, String username) {
        log.trace("PackageServiceImpl.takePackage");
        findDriver.takePackage(userRepository.findByName(username).get(0), missionRepository.findById(missionId).get());
    }

    @Override
    public void dropPackageToHost(long missionId, String username) {
        log.trace("PackageServiceImpl.dropPackageToHost");
        findPackageHost.dropPackage(userRepository.findByName(username).get(0), missionRepository.findById(missionId).get());
    }

    @Override
    public void takePackageFromHost(long missionId, String username) {
        log.trace("PackageServiceImpl.takePackageFromHost");
        findPackageHost.takePackage(userRepository.findByName(username).get(0), missionRepository.findById(missionId).get());
    }
}
