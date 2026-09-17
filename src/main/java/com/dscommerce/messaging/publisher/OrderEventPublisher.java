package com.dscommerce.messaging.publisher;

import com.dscommerce.entities.Order;
import com.dscommerce.entities.User;
import com.dscommerce.messaging.RabbitMQConstants;
import com.dscommerce.messaging.event.OrderCreatedEvent;
import com.dscommerce.messaging.event.OrderItemData;
import com.dscommerce.messaging.mapper.OrderCreatedEventMapper;
import com.dscommerce.messaging.payload.OrderConfirmationMessage;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OrderEventPublisher {

    private final RabbitTemplate rabbitTemplate;
    private final OrderCreatedEventMapper orderCreatedEventMapper;

    public OrderEventPublisher(RabbitTemplate rabbitTemplate, OrderCreatedEventMapper orderCreatedEventMapper) {
        this.rabbitTemplate = rabbitTemplate;
        this.orderCreatedEventMapper = orderCreatedEventMapper;
    }

    public void publishOrderCreated(User user, Order order) {
        List<OrderItemData> items = order.getItems().stream()
                .map(item -> new OrderItemData(item.getProduct().getName(),
                        item.getQuantity(), item.getPrice()))
                .toList();

        double total = items.stream().mapToDouble(
                i -> i.price() * i.quantity()).sum();

        OrderCreatedEvent event = new OrderCreatedEvent(order.getId(),
                user.getName(), user.getEmail(), order.getMoment(),
                order.getStatus(), items, total
        );
        OrderConfirmationMessage message = orderCreatedEventMapper.toMessage(event);

        rabbitTemplate.convertAndSend(RabbitMQConstants.EXG_ORDER_TOPIC,
                RabbitMQConstants.RK_ORDER_CREATED, message);
    }
}
