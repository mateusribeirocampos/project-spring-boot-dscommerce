package com.dscommerce.messaging.event;

import com.dscommerce.entities.enums.OrderStatus;

import java.time.Instant;
import java.util.List;

public record OrderCreatedEvent(
        Long orderId,
        String clientName,
        String clientEmail,
        Instant moment,
        OrderStatus status,
        List<OrderItemData> orderItems,
        Double total
) {
    public OrderCreatedEvent {
        orderItems = List.copyOf(orderItems);
    }
}
