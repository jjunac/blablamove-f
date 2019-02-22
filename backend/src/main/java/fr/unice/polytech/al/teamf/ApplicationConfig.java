package fr.unice.polytech.al.teamf;

import com.googlecode.jsonrpc4j.spring.AutoJsonRpcServiceImplExporter;
import fr.unice.polytech.al.teamf.aspects.ExceptionLogger;
import fr.unice.polytech.al.teamf.interceptors.LoggerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@PropertySource("/blablamove.properties")
@EnableAspectJAutoProxy
public class ApplicationConfig implements WebMvcConfigurer {

    @Bean
    public static AutoJsonRpcServiceImplExporter autoJsonRpcServiceImplExporter() {
        return new AutoJsonRpcServiceImplExporter();
    }

    @Bean
    public ExceptionLogger myAspect() {
        return new ExceptionLogger();
    }
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoggerInterceptor());
    }
}
