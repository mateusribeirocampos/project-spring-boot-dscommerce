package com.dscommerce.messaging.publisher;

import com.dscommerce.messaging.RabbitMQConstants;
import com.dscommerce.messaging.event.OrderCreatedEvent;
import com.dscommerce.messaging.mapper.OrderCreatedEventMapper;
import com.dscommerce.messaging.payload.OrderConfirmationMessage;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class OrderEventPublisher {

    private final RabbitTemplate rabbitTemplate;
    private final OrderCreatedEventMapper orderCreatedEventMapper;

    public OrderEventPublisher(RabbitTemplate rabbitTemplate, OrderCreatedEventMapper orderCreatedEventMapper) {
        this.rabbitTemplate = rabbitTemplate;
        this.orderCreatedEventMapper = orderCreatedEventMapper;
    }

    public void publishOrderCreated(OrderCreatedEvent event) {
        OrderConfirmationMessage message = orderCreatedEventMapper.toMessage(event);
        rabbitTemplate.convertAndSend(RabbitMQConstants.EXG_ORDER_TOPIC,
                RabbitMQConstants.RK_ORDER_CREATED, message);
    }
}
