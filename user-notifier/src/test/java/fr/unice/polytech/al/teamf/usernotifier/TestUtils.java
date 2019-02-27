package fr.unice.polytech.al.teamf.usernotifier;

import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.BeanFactory;

public class TestUtils {
    public static RabbitTemplate queueAndExchangeSetup(BeanFactory context, String queueName) {
        RabbitAdmin rabbitAdmin = context.getBean(RabbitAdmin.class);

        Queue queue = new Queue(queueName, false);
        rabbitAdmin.declareQueue(queue);

        return context.getBean(RabbitTemplate.class);
    }
}
