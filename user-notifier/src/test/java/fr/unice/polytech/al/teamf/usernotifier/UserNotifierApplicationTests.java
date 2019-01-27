package fr.unice.polytech.al.teamf.usernotifier;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fridujo.rabbitmq.mock.MockConnectionFactory;
import fr.unice.polytech.al.teamf.usernotifier.components.UserNotifierBean;
import fr.unice.polytech.al.teamf.usernotifier.entities.Notification;
import fr.unice.polytech.al.teamf.usernotifier.entities.User;
import fr.unice.polytech.al.teamf.usernotifier.repositories.NotificationRepository;
import fr.unice.polytech.al.teamf.usernotifier.repositories.UserRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.lang.reflect.Array;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static fr.unice.polytech.al.teamf.usernotifier.TestUtils.queueAndExchangeSetup;
import static java.time.Duration.ofMillis;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserNotifierApplicationTests {
    
    
    UserRepository userRepository;
    NotificationRepository notificationRepository;
    private RabbitTemplate rabbitTemplate;
    private NotificationReceiver notificationReceiver;
    private UserNotifierBean notifyUser;
    
    @BeforeAll
    void setUpAll() {
        userRepository = Mockito.mock(UserRepository.class);
        notificationRepository = Mockito.mock(NotificationRepository.class);
        notifyUser = new UserNotifierBean(notificationRepository, userRepository);
        notificationReceiver = new NotificationReceiver(notifyUser);
        
    }
    
    @Test
    void testReceive() throws JsonProcessingException, InterruptedException {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(TestConfig.class)) {
            rabbitTemplate = TestUtils.queueAndExchangeSetup(context,
                    "notifications");
            SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
            container.setConnectionFactory(context.getBean(ConnectionFactory.class));
            container.setQueueNames("notifications");
            container.setMessageListener(new MessageListenerAdapter(notificationReceiver, "receive"));
            Mockito.when(userRepository.save(any(User.class))).then(invocation -> invocation.getArgument(0));
            Mockito.when(userRepository.findByName(anyString())).then(invocation -> Collections.singletonList(new User(invocation.getArgument(0))));
            
            List<Notification> notifications = new ArrayList<>();
            Mockito.when(notificationRepository.save(any(Notification.class))).then(invocation -> {
                notifications.add(invocation.getArgument(0));
                return invocation.getArgument(0);
            });
            Mockito.when(notificationRepository.findByUser(any(User.class))).then(invocation -> notifications.stream().filter(notif -> {
                User argument = invocation.getArgument(0);
                return notif.getUser().getName().equals(argument.getName());
            }).collect(Collectors.toList()));
            try {
                container.start();
                Map<String, String> obj = new HashMap<>();
                obj.put("username", "test");
                obj.put("message", "test_message");
                String message = new ObjectMapper().writeValueAsString(obj);
                rabbitTemplate.convertAndSend("notifications", message.getBytes());
                
                Thread.sleep(500); // Dirty wait for async call no other ways for now
                
                Mockito.verify(notificationRepository, Mockito.atLeastOnce()).save(Mockito.any(Notification.class));
                
                User test = new User("test");
                assertThat(notifyUser.pullNotificationForUser(test)).hasSize(1);
                assertThat(notifyUser.pullNotificationForUser(test).get(0)).hasFieldOrPropertyWithValue("message", "test_message");
                
                
            } finally {
                container.stop();
            }
        }
    }
    
    
    static class Receiver {
        private final List<String> messages = new ArrayList<>();
        
        public void receiveMessage(String message) {
            this.messages.add(message);
        }
        
        List<String> getMessages() {
            return messages;
        }
    }
    
}

