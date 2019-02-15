package fr.unice.polytech.al.teamf.aspects;

import fr.unice.polytech.al.teamf.chaosmonkey.ChaosMonkey;
import fr.unice.polytech.al.teamf.webservices.PackageServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public aspect PacakgeServiceImplAspect {

    private final Logger logger = LoggerFactory.getLogger(PackageServiceImpl.class);

    pointcut callMissionFinished(long missionId)
            : execution(boolean fr.unice.polytech.al.teamf.webservice.PackageServiceImpl.missionFinished(..))
            && args(missionId);

    boolean around(long missionId)
            : callMissionFinished(missionId) {
        if (ChaosMonkey.getInstance().draw("mission_finished").hasFailed())
            return false;
        return proceed(missionId);
    }
    
    pointcut callAnswerToPendingMission(long missionId, String username, boolean answer)
            : execution(boolean fr.unice.polytech.al.teamf.webservice.PackageServiceImpl.answerToPendingMission(..))
            && args(missionId, username, answer);

    boolean around(long missionId, String username, boolean answer)
            : callAnswerToPendingMission(missionId, username, answer) {
        if (ChaosMonkey.getInstance().draw("answer_pending_mission").hasFailed())
            return false;
        return proceed(missionId, username, answer);
    }
    
    pointcut callAnswerToPendingPackageHosting(long parcelId, String username, boolean answer)
            : execution(boolean fr.unice.polytech.al.teamf.webservice.PackageServiceImpl.answerToPendingPackageHosting(..))
            && args(long parcelId, String username, boolean answer);

    boolean around(long parcelId, String username, boolean answer)
            : callAnswerToPendingPackageHosting(parcelId, username, answer) {
        if (ChaosMonkey.getInstance().draw("answer_pending_package_host").hasFailed())
            return false;
        return proceed(parcelId, username, answer);
    }
    
    pointcut callTakePackage(long missionId, String username)
            : execution(boolean fr.unice.polytech.al.teamf.webservice.PackageServiceImpl.takePackage(..))
            && args(missionId, username);

    boolean around(long missionId, String username)
            : callTakePackage(missionId, username) {
        if (ChaosMonkey.getInstance().draw("take_package").hasFailed())
            return false;
        return proceed(missionId, username);
    }
    
    pointcut callDropPackageToHost(long parcelId, String username)
            : execution(boolean fr.unice.polytech.al.teamf.webservice.PackageServiceImpl.dropPackageToHost(..))
            && args(parcelId, username);

    boolean around(long parcelId, String username)
            : callDropPackageToHost(parcelId, username) {
        if (ChaosMonkey.getInstance().draw("drop_package_to_host").hasFailed())
            return false;
        return proceed(parcelId, username);
    }
    
    pointcut callTakePackageFromHost(long parcelId, String username)
            : execution(boolean fr.unice.polytech.al.teamf.webservice.PackageServiceImpl.takePackageFromHost(..))
            && args(parcelId, username);

    boolean around(long parcelId, String username)
            : callTakePackageFromHost(parcelId, username) {
        if (ChaosMonkey.getInstance().draw("take_package_from_host").hasFailed())
            return false;
        return proceed(parcelId, username);
    }

}
