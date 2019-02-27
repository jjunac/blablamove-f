package fr.unice.polytech.al.teamf.aspects;

import fr.unice.polytech.al.teamf.Application;
import fr.unice.polytech.al.teamf.chaosmonkey.ChaosMonkey;
import fr.unice.polytech.al.teamf.chaosmonkey.exceptions.ConnectionException;

public aspect ChaosMonkeyInit {
    pointcut callRunApplication(Application application): execution(void fr.unice.polytech.al.teamf.Application.run(..)) && this(application);
    
    before(Application application) throws ConnectionException : callRunApplication(application) {
        ChaosMonkey.getInstance().initialize(application.chaos_monkey_url + "/settings", application.rabbitmq_host);
        
    }
}
