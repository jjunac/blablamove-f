package fr.unice.polytech.al.teamf.usernotifier;

import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

//@Slf4j
@Component
@RabbitListener(queues = "notifications")
public class NotificationReceiver {
//    private final NotifyUser notifyUser;
//
//    @Autowired
//    public NotificationReceiver(NotifyUser notifyUser) {
//        log.info("teeeeeeeeeeeeeeeest");
//        this.notifyUser = notifyUser;
//    }
    
    @RabbitHandler
    public void receive(String in) {
        System.out.println(" [x] Received '" + in + "'");
//        log.info(" [x] Received '" + in + "'");
//        notifyUser.notifyUser("lol", "lol");
    }
}
