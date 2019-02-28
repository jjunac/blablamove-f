package fr.unice.polytech.al.teamf.aspects;

import fr.unice.polytech.al.teamf.Receiver;
import fr.unice.polytech.al.teamf.chaosmonkey.ChaosMonkey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public aspect InsuranceGatewayAspect {
    private final Logger logger = LoggerFactory.getLogger(Receiver.class);


    pointcut callReceiveMessage(String message)
            : execution(void fr.unice.polytech.al.teamf.Receiver.receiveMessage(..))
            && args(message);

    void around(String message)
            : callReceiveMessage(message) {
        if (ChaosMonkey.getInstance().draw("insurance_gateway").hasSucceeded()){
            proceed(message);
        } else {
            logger.info("Request to Insurance Gateway failed due to Chaos Monkey");
            throw new RuntimeException("Chaos Monkey");
        }
    }
}
