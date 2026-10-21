package com.dscommerce.messaging.payload;

import java.time.Instant;
import java.util.List;
import java.math.BigDecimal;

public record OrderConfirmationMessage(
        Long orderId,
        String clientName,
        String clientEmail,
        Instant moment,
        OrderConfirmationStatus status,
        List<OrderConfirmationItem> items,
        BigDecimal total
) {
    public OrderConfirmationMessage {
        items = List.copyOf(items);
    }
}
