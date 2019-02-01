package fr.unice.polytech.al.teamf.chaosmonkey;

import java.util.Random;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Draw {

    private static final Logger log = LoggerFactory.getLogger(Draw.class);

    public final double failProbability;
    public final double draw;

    public Draw(double failProbability, String setting) {
        this.failProbability = failProbability;
        this.draw = new Random().nextDouble();
        if(hasSucceeded()) {
            log.info("You are lucky this time, normal behavior for " + setting);
        } else {
            log.info("IT'S CHAOS TIME !!! " + setting + "failed !");
        }
    }

    public boolean hasFailed() {
        return draw < failProbability;
    }

    public boolean hasSucceeded() {
        return draw >= failProbability;
    }
}
