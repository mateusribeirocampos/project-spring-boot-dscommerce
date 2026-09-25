package com.dscommerce.messaging.payload;

import java.time.Instant;
import java.util.List;

public record OrderConfirmationMessage(
        Long orderId,
        String clientName,
        String clientEmail,
        Instant moment,
        OrderConfirmationStatus status,
        List<OrderConfirmationItem> orderItems,
        Double total
) {
    public OrderConfirmationMessage {
        orderItems = List.copyOf(orderItems);
    }
}
