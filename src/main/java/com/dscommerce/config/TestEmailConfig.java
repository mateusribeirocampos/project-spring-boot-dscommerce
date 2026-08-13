package com.dscommerce.config;

import com.dscommerce.services.email.EmailService;
import com.dscommerce.services.email.MockEmailService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile({"test", "it"})
public class TestEmailConfig {

    @Bean
    EmailService emailService() {
        return new MockEmailService();
    }
}
