package fr.unice.polytech.al.teamf.webservices;

import com.googlecode.jsonrpc4j.spring.AutoJsonRpcServiceImpl;
import fr.unice.polytech.al.teamf.ComputePoints;
import fr.unice.polytech.al.teamf.FindDriver;
import fr.unice.polytech.al.teamf.FindPackageHost;
import fr.unice.polytech.al.teamf.*;
import fr.unice.polytech.al.teamf.entities.Mission;
import fr.unice.polytech.al.teamf.entities.Parcel;
import fr.unice.polytech.al.teamf.exceptions.UnknownUserException;
import fr.unice.polytech.al.teamf.repositories.MissionRepository;
import fr.unice.polytech.al.teamf.repositories.ParcelRepository;
import fr.unice.polytech.al.teamf.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

@Slf4j
@Service
@AutoJsonRpcServiceImpl
@Transactional
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
        Optional<Mission> missionOptional = missionRepository.findById(missionId);
        if (missionOptional.isPresent()) {
            Mission mission = missionOptional.get();
            return managePackage.missionFinished(mission);
        } else {
            log.error(String.format("Mission %d is not in the database", missionId));
        }
        return false;
    }
    
    @Override
    public boolean answerToPendingMission(long missionId, String username, boolean answer) {
        log.trace("PackageServiceImpl.answerToPendingMission");
        return answerMission.answerToPendingMission(missionId, username, answer);
    }
    
    @Override
    public boolean answerToPendingPackageHosting(long parcelId, String username, boolean answer) {
        log.trace("PackageServiceImpl.answerToPendingPackageHosting");
        return answerPackageHosting.answerToPendingPackageHosting(parcelRepository.findById(parcelId).get(), userRepository.findByName(username).get(0), answer);
    }
    
    @Override
    public boolean takePackage(long missionId, String username) {
        log.trace("PackageServiceImpl.takePackage");
        return managePackage.takePackageFromDriver(userRepository.findByName(username).get(0), missionRepository.findById(missionId).get());
    }
    
    @Override
    public boolean dropPackageToHost(long parcelId, String username) {
        log.trace("PackageServiceImpl.dropPackageToHost");
        return managePackage.dropPackageToHost(userRepository.findByName(username).get(0), parcelRepository.findById(parcelId).get());
    }
    
    @Override
    public boolean takePackageFromHost(long parcelId, String username) {
        log.trace("PackageServiceImpl.takePackageFromHost");
        return managePackage.takePackageFromHost(userRepository.findByName(username).get(0), parcelRepository.findById(parcelId).get());
    }
    
    @Override
    public long getPackageMissionId(long parcelId) {
        log.debug("missions " + StreamSupport.stream(missionRepository.findAll().spliterator(), false).map(mission -> {
            List<Long> res = new ArrayList<>();
            res.add(mission.getId());
            res.add(mission.getParcel().getId());
            res.add(mission.getParcel().getMission().getId());
            return res;
        }));
        Optional<Parcel> optionalParcel = parcelRepository.findById(parcelId);
        return optionalParcel.map(parcel -> parcel.getMission().getId()).orElse(-1L);
    }
}
