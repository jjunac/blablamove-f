package fr.unice.polytech.al.teamf.chaosmonkey;

import fr.unice.polytech.al.teamf.chaosmonkey.exceptions.ConnectionException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ChaosMonkeyTest {

    @Test
    void test() throws Exception {
        // TODO do a real test
        ChaosMonkey.getInstance().intialize("http://localhost:5008/settings");
        System.out.println(ChaosMonkey.getInstance().draw("notify_car_crash").hasFailed());
        System.out.println(ChaosMonkey.getInstance().draw("notify_car_crash").hasFailed());
        System.out.println(ChaosMonkey.getInstance().draw("notify_car_crash").hasFailed());
        System.out.println(ChaosMonkey.getInstance().draw("notify_car_crash").hasFailed());
        System.out.println(ChaosMonkey.getInstance().draw("notify_car_crash").hasFailed());
        System.out.println(ChaosMonkey.getInstance().draw("notify_car_crash").hasFailed());
        System.out.println(ChaosMonkey.getInstance().draw("notify_car_crash").hasFailed());
        System.out.println(ChaosMonkey.getInstance().draw("notify_car_crash").hasFailed());
        System.out.println(ChaosMonkey.getInstance().draw("notify_car_crash").hasFailed());
        System.out.println(ChaosMonkey.getInstance().draw("notify_car_crash").hasFailed());
        System.out.println(ChaosMonkey.getInstance().draw("notify_car_crash").hasFailed());
    }

    @Test
    void test2() throws Exception {
        ChaosMonkey.getInstance().intialize("http://localhost:5008/settings");
        while(true) {
            System.out.println(ChaosMonkey.getInstance().draw("notify_package_hosting").hasFailed());
            Thread.sleep(1000);
        }
    }
}