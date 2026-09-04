package com.dscommerce.messaging.event;

public record OrderItemData(
        String productName,
        Integer quantity,
        Double price
) {
}
