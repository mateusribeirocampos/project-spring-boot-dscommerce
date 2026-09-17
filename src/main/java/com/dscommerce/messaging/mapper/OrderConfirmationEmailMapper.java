package com.dscommerce.messaging.mapper;

import com.dscommerce.dto.EmailDTO;
import com.dscommerce.messaging.payload.OrderConfirmationMessage;
import com.dscommerce.services.email.EmailFactory;
import org.springframework.stereotype.Component;

@Component
public class OrderConfirmationEmailMapper {

    private final EmailFactory emailFactory;

    public OrderConfirmationEmailMapper(EmailFactory emailFactory) {
        this.emailFactory = emailFactory;
    }

    public EmailDTO toEmailDTO(OrderConfirmationMessage message) {

        return emailFactory.buildOrderConfirmationEmail(message.orderId(),
                message.clientName(), message.clientEmail(), message.moment(),
                message.status(), message.orderItems(),message.total());
    }
}
