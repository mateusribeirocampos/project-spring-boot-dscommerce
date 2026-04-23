package com.dscommerce.services.email;

import com.dscommerce.dto.EmailDTO;

public interface EmailService {

    void plainTextEmail(EmailDTO dto);
}
