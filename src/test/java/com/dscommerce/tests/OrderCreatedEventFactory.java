package com.dscommerce.tests;

import com.dscommerce.entities.enums.OrderStatus;
import com.dscommerce.messaging.event.OrderCreatedEvent;
import com.dscommerce.messaging.event.OrderItemData;

import java.time.Instant;
import java.util.List;

public class OrderCreatedEventFactory {

    public static OrderCreatedEvent orderCreatedEvent() {
        List<OrderItemData> dataList = List.of(new OrderItemData("Mouse", 2, 100.00));
        return new OrderCreatedEvent(7L, "Maria", "maria@x.com",
                Instant.parse("2026-09-23T17:16:57.000000000Z"), OrderStatus.WAITING_PAYMENT, dataList, 200.00);
    }

    public static OrderCreatedEvent orderCreatedEvent(OrderStatus status) {
        List<OrderItemData> dataList = List.of(new OrderItemData("Flash drive", 3, 250.00));
        return new OrderCreatedEvent(7L, "Maria", "maria@x.com",
                Instant.parse("2026-09-23T17:16:57.000000000Z"), status, dataList, 3000.00);
    }
}
