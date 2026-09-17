package com.dscommerce.messaging.consumer;

import com.dscommerce.dto.EmailDTO;
import com.dscommerce.messaging.RabbitMQConstants;
import com.dscommerce.messaging.mapper.OrderConfirmationEmailMapper;
import com.dscommerce.messaging.payload.OrderConfirmationMessage;
import com.dscommerce.services.email.EmailService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class OrderConfirmationConsumer {

    private final OrderConfirmationEmailMapper mapper;
    private final EmailService emailService;

    public OrderConfirmationConsumer(OrderConfirmationEmailMapper mapper, EmailService emailService) {
        this.mapper = mapper;
        this.emailService = emailService;
    }

    @RabbitListener(queues = RabbitMQConstants.QUEUE_ORDER_CONFIRMATION)
    public void listen(OrderConfirmationMessage message) {git
        EmailDTO dto = mapper.toEmailDTO(message);
        emailService.plainTextEmail(dto);
    }
}
