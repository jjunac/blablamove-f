package fr.unice.polytech.al.teamf.components;

import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.BeanFactory;

public class TestUtils {
    public static RabbitTemplate queueAndExchangeSetup(BeanFactory context, String queueName, String exchangeName, String routingKey) {
        RabbitAdmin rabbitAdmin = context.getBean(RabbitAdmin.class);

        Queue queue = new Queue(queueName, false);
        rabbitAdmin.declareQueue(queue);
        TopicExchange exchange = new TopicExchange(exchangeName);
        rabbitAdmin.declareExchange(exchange);
        rabbitAdmin.declareBinding(BindingBuilder.bind(queue).to(exchange).with(routingKey));

        return context.getBean(RabbitTemplate.class);
    }
}
