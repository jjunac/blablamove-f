package fr.unice.polytech.al.teamf;

import fr.unice.polytech.al.teamf.chaosmonkey.ChaosMonkey;
import fr.unice.polytech.al.teamf.components.MessageReceiver;
import fr.unice.polytech.al.teamf.entities.GPSCoordinate;
import fr.unice.polytech.al.teamf.entities.Mission;
import fr.unice.polytech.al.teamf.entities.Parcel;
import fr.unice.polytech.al.teamf.entities.User;
import fr.unice.polytech.al.teamf.repositories.MissionRepository;
import fr.unice.polytech.al.teamf.repositories.ParcelRepository;
import fr.unice.polytech.al.teamf.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import javax.transaction.Transactional;

@SpringBootApplication
public class Application implements CommandLineRunner {

    private final Logger logger = LoggerFactory.getLogger(getClass());


    @Autowired
    UserRepository userRepository;
    @Autowired
    ParcelRepository parcelRepository;
    @Autowired
    MissionRepository missionRepository;

    static final String notificationsQueueName = "notifications";
    static final String notificationsTopicExchangeName = "notifications-exchange";

    @Bean
    public Queue notificationsQueue() {
        return new Queue(notificationsQueueName);
    }

    @Bean
    TopicExchange notificationsExchange() {
        return new TopicExchange(notificationsTopicExchangeName);
    }

    @Bean
    public Binding bindingNotification(TopicExchange notificationsExchange, Queue notificationsQueue) {
        return BindingBuilder.bind(notificationsQueue).to(notificationsExchange).with("notifications.#");
    }

    //TODO Add notification queue name in container config

    static final String topicExchangeName = "external-exchange";

    static final String queueName = "external";

    @Value("${chaos_monkey_address}")
    public String chaos_monkey_url;

    @Bean
    Queue queue() {
        return new Queue(queueName, false);
    }

    @Bean
    TopicExchange exchange() {
        return new TopicExchange(topicExchangeName);
    }

    @Bean
    Binding binding(Queue queue, TopicExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with("external.#");
    }

    @Bean
    SimpleMessageListenerContainer pointPricingContainer(ConnectionFactory connectionFactory,
                                             MessageListenerAdapter listenerAdapter) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueueNames(queueName);
        container.setMessageListener(listenerAdapter);
        return container;
    }

    @Bean
    MessageListenerAdapter pointPricingListenerAdapter(MessageReceiver receiver) {
        return new MessageListenerAdapter(receiver, "receiveMessage");
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Override
    @Transactional
    public void run(String... arg0) throws Exception {
        ChaosMonkey.getInstance().initialize(chaos_monkey_url + "/settings");

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
