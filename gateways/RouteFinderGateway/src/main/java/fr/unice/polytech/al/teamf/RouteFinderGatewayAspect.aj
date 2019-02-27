package fr.unice.polytech.al.teamf;

import fr.unice.polytech.al.teamf.chaosmonkey.ChaosMonkey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public aspect RouteFinderGatewayAspect {

    private final Logger logger = LoggerFactory.getLogger(Receiver.class);


    pointcut callRouteFinderReceiveMessage(String message)
            : execution(boolean fr.unice.polytech.al.teamf.Receiver.receiveMessage(..))
            && args(message);

    void around(String message)
            : callRouteFinderReceiveMessage(message) {
        if (ChaosMonkey.getInstance().draw("route_finder_gateway").hasFailed()) {
            logger.info("The chaos monkey told me to crash \uD83D\uDE22");
            return;
        }
        proceed(message);
        return ;
    }
}
