package com.dscommerce.messaging.payload;

public record OrderConfirmationItem(
        String productName,
        Integer quantity,
        Double price
) {
}
