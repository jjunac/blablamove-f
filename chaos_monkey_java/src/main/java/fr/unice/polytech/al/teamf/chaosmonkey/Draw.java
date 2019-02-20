package fr.unice.polytech.al.teamf.chaosmonkey;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Random;

import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Draw {

    private static final Logger log = LoggerFactory.getLogger(Draw.class);

    public final double failProbability;
    public final double draw;


    public Draw(double failProbability, String setting, Channel logChannel) {
        String message = setting;
        try {
            System.out.println(setting+" "+logChannel);
            logChannel.basicPublish("chaos_logs_exchange", "", null, message.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.failProbability = failProbability;
        this.draw = new Random().nextDouble();
        if (hasSucceeded()) {
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
