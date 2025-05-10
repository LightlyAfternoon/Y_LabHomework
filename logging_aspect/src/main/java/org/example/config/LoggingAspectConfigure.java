package org.example.config;

import org.example.aspect.LoggableAspect;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LoggingAspectConfigure {
    @Bean
    public LoggableAspect loginAspect() {
        return new LoggableAspect();
    }
}