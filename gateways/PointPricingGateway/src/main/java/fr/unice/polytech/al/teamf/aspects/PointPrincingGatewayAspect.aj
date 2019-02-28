package fr.unice.polytech.al.teamf.aspects;

import fr.unice.polytech.al.teamf.Receiver;
import fr.unice.polytech.al.teamf.chaosmonkey.ChaosMonkey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public aspect PointPrincingGatewayAspect {

    private final Logger logger = LoggerFactory.getLogger(Receiver.class);


    pointcut receive(String message)
            : execution(void fr.unice.polytech.al.teamf.Receiver.receiveMessage(..))
            && args(message);

    void around(String message)
            : receive(message) {
        if (ChaosMonkey.getInstance().draw("point_pricing").hasFailed()){
            logger.info("The chaos monkey cut the connection to the point pricing service");
            throw new RuntimeException("Chaos Monkey");
        } else {
            proceed(message);
        }
    }
}
