package fr.unice.polytech.al.teamf.webservices;

import com.googlecode.jsonrpc4j.spring.AutoJsonRpcServiceImpl;
import fr.unice.polytech.al.teamf.AnswerMission;
import fr.unice.polytech.al.teamf.ComputePoints;
import fr.unice.polytech.al.teamf.FindDriver;
import fr.unice.polytech.al.teamf.FindPackageHost;
import fr.unice.polytech.al.teamf.entities.Mission;
import fr.unice.polytech.al.teamf.exceptions.UnknownUserException;
import fr.unice.polytech.al.teamf.repositories.MissionRepository;
import fr.unice.polytech.al.teamf.repositories.ParcelRepository;
import fr.unice.polytech.al.teamf.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    AnswerMission answerMission;
    @Autowired
    FindPackageHost findPackageHost;
    @Autowired
    ParcelRepository parcelRepository;


    @Override
    public boolean computePoints(long missionId) {
        log.trace("PackageDroppedImpl.computePoints");
        Optional <Mission> mission = missionRepository.findById(missionId);
        if (mission.isPresent()) {
            try {
                computePoints.computePoints(mission.get());
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
        return answerMission.answerToPendingMission(missionRepository.findById(missionId).get(), userRepository.findByName(username).get(0), answer);
    }

    @Override
    public boolean answerToPendingPackageHosting(long parcelId, String username, boolean answer) {
        log.trace("PackageServiceImpl.answerToPendingPackageHosting");
        return findPackageHost.answerToPendingPackageHosting(parcelRepository.findById(parcelId).get(), userRepository.findByName(username).get(0), answer);
    }
}
