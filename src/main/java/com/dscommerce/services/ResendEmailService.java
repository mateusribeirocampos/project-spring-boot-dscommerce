package com.dscommerce.services;

import com.dscommerce.dto.EmailDTO;
import com.dscommerce.services.exceptions.EmailException;
import com.resend.Resend;
import com.resend.core.exception.ResendException;
import com.resend.services.emails.model.CreateEmailOptions;
import com.resend.services.emails.model.CreateEmailResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;

public class ResendEmailService implements EmailService {

    private static final Logger LOG = LoggerFactory.getLogger(ResendEmailService.class);

    @Autowired
    private Resend resend;

    @Async("emailTaskExecutor")
    @Override
    public void plainTextEmail(EmailDTO dto) {

        CreateEmailOptions.Builder builder = CreateEmailOptions.builder()
                .from(dto.getFromEmail())
                .to(dto.getToEmail())
                .subject(dto.getSubject());

        if (dto.getContentType().equals("text/plain")) {
            builder.text(dto.getBody());
        } else {
            builder.html(dto.getBody());
        }

        CreateEmailOptions params = builder.build();

        try {
            LOG.info("Sending email to: {}", dto.getToEmail());
            CreateEmailResponse response = resend.emails().send(params);
            LOG.info(response.getId());
            LOG.info("Email sent with success!");
        } catch (ResendException e) {
            LOG.error("Failed to send email to {}: {}", dto.getToEmail(), e.getMessage());
        }
    }
}
