package com.dscommerce.messaging.event;

import com.dscommerce.entities.enums.OrderStatus;

import java.time.Instant;
import java.util.List;
import java.math.BigDecimal;

public record OrderCreatedEvent(
        Long orderId,
        String clientName,
        String clientEmail,
        Instant moment,
        OrderStatus status,
        List<OrderItemData> items,
        BigDecimal total
) {
    public OrderCreatedEvent {
        items = List.copyOf(items);
    }
}
