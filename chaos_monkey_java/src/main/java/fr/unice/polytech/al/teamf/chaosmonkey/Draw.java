package fr.unice.polytech.al.teamf.chaosmonkey;

import java.util.Random;

public class Draw {

    public final double failProbability;
    public final double draw;

    public Draw(double failProbability) {
        this.failProbability = failProbability;
        this.draw = new Random().nextDouble();
    }

    public boolean hasFailed() {
        return draw < failProbability;
    }

    public boolean hasSucceeded() {
        return draw >= failProbability;
    }
}
