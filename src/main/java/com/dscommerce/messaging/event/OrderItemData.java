package com.dscommerce.messaging.event;

import java.math.BigDecimal;

public record OrderItemData(
        String productName,
        Integer quantity,
        BigDecimal price
) {
}
