package com.dscommerce.config;

import com.dscommerce.services.EmailService;
import com.dscommerce.services.MockEmailService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("test")
public class TestEmailConfig {

    @Bean
    EmailService emailService() {
        return new MockEmailService();
    }
}
