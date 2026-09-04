package com.dscommerce.messaging.payload;

import com.dscommerce.entities.enums.OrderStatus;

import java.time.Instant;
import java.util.List;

public record OrderConfirmationMessage(
        Long orderId,
        String clientName,
        String clientEmail,
        Instant moment,
        OrderStatus status,
        List<OrderConfirmationItem> orderItems,
        Double total
) {
    public OrderConfirmationMessage {
        orderItems = List.copyOf(orderItems);
    }
}
