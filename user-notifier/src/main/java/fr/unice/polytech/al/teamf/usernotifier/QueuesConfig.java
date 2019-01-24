package fr.unice.polytech.al.teamf.usernotifier;

import com.googlecode.jsonrpc4j.spring.AutoJsonRpcServiceImplExporter;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class QueuesConfig {

    @Bean
    public Queue hello() {
        return new Queue("notifications");
    }
    
    @Bean
    public NotificationReceiver receiver1() {
        return new NotificationReceiver();
    }
    @Bean
    public RabbitTemplate rabbitTemplate(final ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
//        rabbitTemplate.setMessageConverter();
        return rabbitTemplate;
    }
    
    @Bean
    public AutoJsonRpcServiceImplExporter producerJackson2MessageConverter() {
        return new AutoJsonRpcServiceImplExporter();
    }
}
