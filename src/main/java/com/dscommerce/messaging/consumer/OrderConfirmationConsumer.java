package com.dscommerce.messaging.consumer;

import com.dscommerce.dto.EmailDTO;
import com.dscommerce.messaging.RabbitMQConstants;
import com.dscommerce.messaging.payload.OrderConfirmationMessage;
import com.dscommerce.services.email.EmailFactory;
import com.dscommerce.services.email.EmailService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class OrderConfirmationConsumer {

    private final EmailFactory emailFactory;
    private final EmailService emailService;

    public OrderConfirmationConsumer(EmailFactory emailFactory, EmailService emailService) {
        this.emailFactory = emailFactory;
        this.emailService = emailService;
    }

    @RabbitListener(queues = RabbitMQConstants.QUEUE_ORDER_CONFIRMATION)
    public void listen(OrderConfirmationMessage message) {
        EmailDTO dto = emailFactory.buildOrderConfirmationEmail(message);
        emailService.plainTextEmail(dto);
    }
}
