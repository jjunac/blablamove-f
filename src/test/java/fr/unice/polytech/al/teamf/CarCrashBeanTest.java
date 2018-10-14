package fr.unice.polytech.al.teamf;

import fr.unice.polytech.al.teamf.components.CarCrashBean;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CarCrashBeanTest {

    CarCrashBean bean;

    @BeforeEach
    void setUp() {
        bean = new CarCrashBean();
    }

    @Test
    void notifyUser() {
        bean.notifyUser();
    }
}