package org.example.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Optional;

@Configuration
@EnableJpaAuditing
public class WebConfigure implements WebMvcConfigurer {
    @Bean
    public OpenAPI customOpenApi(@Value("${application.description}")String appDescription,
                                 @Value("${application.version}")String appVersion) {
        return new OpenAPI().info(new Info().title("Application API")
                        .version(appVersion)
                        .description(appDescription));
    }

    @Bean
    public AuditorAware<String> auditProvider() {
        return () -> Optional.of("system");
    }
}