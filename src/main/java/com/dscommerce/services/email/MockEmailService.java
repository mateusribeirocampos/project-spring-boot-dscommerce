package com.dscommerce.services.email;

import com.dscommerce.dto.EmailDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MockEmailService implements EmailService {

    private Logger LOG = LoggerFactory.getLogger(MockEmailService.class);

    @Override
    public void plainTextEmail(EmailDTO dto) {
        LOG.info("Email sent to: {}", dto.getToEmail());
    }
}
