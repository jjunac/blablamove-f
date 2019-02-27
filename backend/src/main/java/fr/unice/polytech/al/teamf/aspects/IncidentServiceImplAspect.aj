package fr.unice.polytech.al.teamf.aspects;

import fr.unice.polytech.al.teamf.chaosmonkey.ChaosMonkey;
import fr.unice.polytech.al.teamf.webservices.IncidentServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public aspect IncidentServiceImplAspect {

    private final Logger logger = LoggerFactory.getLogger(IncidentServiceImpl.class);


    pointcut callNotifyCarCrash(String username, double latitude, double longitude)
            : execution(boolean fr.unice.polytech.al.teamf.webservices.IncidentServiceImpl.notifyCarCrash(..))
            && args(username, latitude, longitude);

    boolean around(String username, double latitude, double longitude)
            : callNotifyCarCrash(username, latitude, longitude) {
        if (ChaosMonkey.getInstance().draw("notify_car_crash").hasFailed())
            return false;
        return proceed(username, latitude, longitude);
    }

}
