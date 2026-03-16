package com.dscommerce.services;

import com.dscommerce.dto.EmailDTO;

public interface EmailService {

    void plainTextEmail(EmailDTO dto);
}
