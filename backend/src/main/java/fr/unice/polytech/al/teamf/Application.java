package fr.unice.polytech.al.teamf;

import fr.unice.polytech.al.teamf.chaosmonkey.ChaosMonkey;
import fr.unice.polytech.al.teamf.entities.GPSCoordinate;
import fr.unice.polytech.al.teamf.entities.Mission;
import fr.unice.polytech.al.teamf.entities.Parcel;
import fr.unice.polytech.al.teamf.entities.User;
import fr.unice.polytech.al.teamf.repositories.MissionRepository;
import fr.unice.polytech.al.teamf.repositories.ParcelRepository;
import fr.unice.polytech.al.teamf.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import javax.transaction.Transactional;

@Slf4j
@SpringBootApplication
public class Application implements CommandLineRunner {

    @Autowired
    UserRepository userRepository;
    @Autowired
    ParcelRepository parcelRepository;
    @Autowired
    MissionRepository missionRepository;

    static final String pointpricingQueueName = "pointpricing-receiving";

    static final String routefindingQueueName = "routefinding-receiving";

    static final String insuranceQueueName = "insurance-receiving";
    static final String notificationsQueueName = "notifications";

    @Bean
    public Queue notificationsQueue() {
        return new Queue(notificationsQueueName);
    }


    @Value("${chaos_monkey_address}")
    public String chaos_monkey_url;

    @Value("${spring.rabbitmq.host}")
    public String rabbitmq_host;


    @Bean
    Queue pointPricingQueue() {
        return new Queue(pointpricingQueueName, false);
    }

    @Bean
    Queue routeFindingQueue() {
        return new Queue(routefindingQueueName, false);
    }

    @Bean
    Queue insuranceQueue() {
        return new Queue(insuranceQueueName, false);
    }

    @RabbitListener(queues = pointpricingQueueName)
    public void listenPointPricing(String message) {
        log.info("point-pricing message : " + message);
    }

    @RabbitListener(queues = insuranceQueueName)
    public void listenInsurance(String message) {
        log.info("insurance message : " + message);
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Override
    @Transactional
    public void run(String... arg0) throws Exception {
        log.info("rabbitmq address "+rabbitmq_host);
        ChaosMonkey.getInstance().initialize(chaos_monkey_url + "/settings", rabbitmq_host);

        User thomas = new User("Thomas");
        userRepository.save(thomas);
        User loic = new User("Loic");
        userRepository.save(loic);
        User jeremy = new User("Jeremy");
        userRepository.save(jeremy);
        User johann = new User("Johann");
        userRepository.save(johann);
        User erick = new User("Erick");
        userRepository.save(erick);
        User julien = new User("Julien");
        userRepository.save(julien);

        Parcel parcel1 = new Parcel(jeremy);
        parcelRepository.save(parcel1);
        Mission jeremysMission = new Mission(johann, jeremy, new GPSCoordinate(10, 12), new GPSCoordinate(10, 42), parcel1);
        jeremysMission.setOngoing();
        parcel1.setMission(jeremysMission);
        missionRepository.save(jeremysMission);
        johann.addTransportedMission(jeremysMission);

        Parcel parcel2 = new Parcel(thomas);
        parcelRepository.save(parcel2);
        Mission thomasMission = new Mission(johann, thomas, new GPSCoordinate(10, 12), new GPSCoordinate(10, 69), parcel2);
        thomasMission.setOngoing();
        parcel2.setMission(thomasMission);
        missionRepository.save(thomasMission);
        johann.addTransportedMission(thomasMission);

    }


}
