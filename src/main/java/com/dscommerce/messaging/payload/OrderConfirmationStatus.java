package com.dscommerce.messaging.payload;

public enum OrderConfirmationStatus {
    WAITING_PAYMENT,
    PAID,
    SHIPPED,
    DELIVERED,
    CANCELED;
}
