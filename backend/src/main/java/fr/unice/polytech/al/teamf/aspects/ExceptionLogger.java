package fr.unice.polytech.al.teamf.aspects;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import java.io.PrintWriter;
import java.io.StringWriter;

@Slf4j
@Aspect
public class ExceptionLogger {
    
    @Autowired
    private RabbitTemplate template;
    
    @Autowired
    private FanoutExchange fanout;
    
    
    @AfterThrowing(value = "execution(* fr.unice.polytech.al.teamf..*(..))", throwing = "error")
    public void logError(JoinPoint jp, Throwable error) {
        StringWriter writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        error.printStackTrace(printWriter);
        template.convertAndSend(fanout.getName(), "", "Exception: " + error.toString() + " in " + jp.getSignature());
    }
}
