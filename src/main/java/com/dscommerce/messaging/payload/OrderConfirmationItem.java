package com.dscommerce.messaging.payload;

import java.math.BigDecimal;

public record OrderConfirmationItem(
        String productName,
        Integer quantity,
        BigDecimal price
) {
}
