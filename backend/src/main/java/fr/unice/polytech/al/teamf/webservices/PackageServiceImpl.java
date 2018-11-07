package fr.unice.polytech.al.teamf.webservices;

import com.googlecode.jsonrpc4j.spring.AutoJsonRpcServiceImpl;
import fr.unice.polytech.al.teamf.ComputePoints;
import fr.unice.polytech.al.teamf.FindDriver;
import fr.unice.polytech.al.teamf.FindPackageHost;
import fr.unice.polytech.al.teamf.*;
import fr.unice.polytech.al.teamf.entities.Mission;
import fr.unice.polytech.al.teamf.entities.User;
import fr.unice.polytech.al.teamf.exceptions.UnknownUserException;
import fr.unice.polytech.al.teamf.repositories.MissionRepository;
import fr.unice.polytech.al.teamf.repositories.ParcelRepository;
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
    AnswerMission answerMission;
    @Autowired
    AnswerPackageHosting answerPackageHosting;
    @Autowired
    FindPackageHost findPackageHost;
    @Autowired
    ParcelRepository parcelRepository;
    @Autowired
    ManagePackage managePackage;


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

    @Override
    public boolean answerToPendingMission(long missionId, String username, boolean answer) {
        log.trace("PackageServiceImpl.answerToPendingMission");
        return answerMission.answerToPendingMission(missionRepository.findById(missionId).get(), userRepository.findByName(username).get(0), answer);
    }

    @Override
    public boolean answerToPendingPackageHosting(long parcelId, String username, boolean answer) {
        log.trace("PackageServiceImpl.answerToPendingPackageHosting");
        return answerPackageHosting.answerToPendingPackageHosting(parcelRepository.findById(parcelId).get(), userRepository.findByName(username).get(0), answer);
    }

    @Override
    public void takePackage(long missionId, String username) {
        log.trace("PackageServiceImpl.takePackage");
        managePackage.takePackageFromDriver(userRepository.findByName(username).get(0), missionRepository.findById(missionId).get());
    }

    @Override
    public void dropPackageToHost(long missionId, String username) {
        // TODO change to parcelId
        log.trace("PackageServiceImpl.dropPackageToHost");
        managePackage.dropPackageToHost(userRepository.findByName(username).get(0), missionRepository.findById(missionId).get());
    }

    @Override
    public void takePackageFromHost(long parcelId, String username) {
        log.trace("PackageServiceImpl.takePackageFromHost");
        managePackage.takePackageFromHost(userRepository.findByName(username).get(0), parcelRepository.findById(parcelId).get());
    }
}
