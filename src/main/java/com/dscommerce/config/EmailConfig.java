package com.dscommerce.config;

import com.dscommerce.services.email.EmailService;
import com.dscommerce.services.email.ResendEmailService;
import com.resend.Resend;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;

@Configuration
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
}
