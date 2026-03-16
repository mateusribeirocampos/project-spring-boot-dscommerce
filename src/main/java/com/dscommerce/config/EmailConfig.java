package com.dscommerce.config;

import com.dscommerce.services.EmailService;
import com.dscommerce.services.ResendEmailService;
import com.resend.Resend;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.concurrent.Executor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
@Profile({"dev", "prod"})
public class EmailConfig {

    @Value("${RESEND_API_KEY}")
    private String apiKey;

    @Bean
    EmailService emailService() {
        return new ResendEmailService();
    }

    @Bean
    public Resend resend() {
        return new Resend(apiKey);
    }

    @Bean(name = "emailTaskExecutor")
    public Executor emailTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2); //thread sempre ativas
        executor.setMaxPoolSize(5); //maximo em pico
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("email-");
        executor.initialize();
        return executor;
    }
}
