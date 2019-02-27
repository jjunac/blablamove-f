package fr.unice.polytech.al.teamf.usernotifier;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.unice.polytech.al.teamf.usernotifier.components.UserNotifierBean;
import fr.unice.polytech.al.teamf.usernotifier.entities.Answer;
import fr.unice.polytech.al.teamf.usernotifier.entities.Notification;
import fr.unice.polytech.al.teamf.usernotifier.entities.User;
import fr.unice.polytech.al.teamf.usernotifier.repositories.NotificationRepository;
import fr.unice.polytech.al.teamf.usernotifier.repositories.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.*;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

public class UserNotifierApplicationTests {
    
    
    private UserRepository userRepository;
    private NotificationRepository notificationRepository;
    private RabbitTemplate rabbitTemplate;
    private UserNotifierBean notifyUser;
    private SimpleMessageListenerContainer container;
    private User user;
    private AnnotationConfigApplicationContext context;
    
    @BeforeEach
    void setUpAll() {
        userRepository = Mockito.mock(UserRepository.class);
        notificationRepository = Mockito.mock(NotificationRepository.class);
        notifyUser = new UserNotifierBean(notificationRepository, userRepository);
        NotificationReceiver notificationReceiver = new NotificationReceiver(notifyUser);
        context = new AnnotationConfigApplicationContext(TestConfig.class);
        rabbitTemplate = TestUtils.queueAndExchangeSetup(context,
                "notifications");
        container = new SimpleMessageListenerContainer();
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
        user = new User("test");
        user.setNotifications(notifications);
        
    }
    
    @AfterEach
    void ta() {
        container.stop();
        context.close();
    }
    
    @Test
    void testReceive() throws JsonProcessingException, InterruptedException {
        
        try {
            container.start();
            assertThat(notifyUser.pullNotificationForUser(user)).hasSize(0);
            ObjectMapper objM = new ObjectMapper();
            ObjectNode root = objM.createObjectNode();
            root.put("username", user.getName());
            root.putObject("message").put("message", "test_message");
            String message = objM.writeValueAsString(root);
            rabbitTemplate.convertAndSend("notifications", message.getBytes());
            
            Thread.sleep(500); // Dirty wait for async call no other ways for now
            
            Mockito.verify(notificationRepository, Mockito.atLeastOnce()).save(Mockito.any(Notification.class));
            
            List<Notification> receivedNotifications = notifyUser.pullNotificationForUser(user);
            assertThat(receivedNotifications).hasSize(1);
            assertThat(receivedNotifications.get(0)).hasFieldOrPropertyWithValue("message", "test_message");
            assertThat(notifyUser.pullNotificationForUser(user)).hasSize(0);
            
        } finally {
            container.stop();
        }
        
    }
    
    @Test
    void testReceiveWithAnswer() throws JsonProcessingException, InterruptedException {
        try {
            container.start();
            assertThat(notifyUser.pullNotificationForUser(user)).hasSize(0);
            ObjectMapper objM = new ObjectMapper();
            ObjectNode root = objM.createObjectNode();
            root.put("username", user.getName());
            HashMap<String, String> parameters = new HashMap<>();
            parameters.put("testparam", "testvalue");
            
            Answer answerToTest = new Answer("/test", "testMethod", parameters);
            root.putObject("message").put("message", "test_message")
                    .set("answer", objM.valueToTree(answerToTest));
            String message = objM.writeValueAsString(root);
            rabbitTemplate.convertAndSend("notifications", message.getBytes());
            
            Thread.sleep(500); // Dirty wait for async call no other ways for now
            
            Mockito.verify(notificationRepository, Mockito.atLeastOnce()).save(Mockito.any(Notification.class));
            
            List<Notification> receivedNotifications = notifyUser.pullNotificationForUser(user);
            assertThat(receivedNotifications).hasSize(1);
            assertThat(receivedNotifications.get(0).getAnswer()).isEqualTo(answerToTest);
            assertThat(notifyUser.pullNotificationForUser(user)).hasSize(0);
            
        } finally {
            container.stop();
        }
    }
    
}

