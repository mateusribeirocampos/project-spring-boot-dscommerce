package com.dscommerce.messaging.mapper;

import com.dscommerce.messaging.event.OrderCreatedEvent;
import com.dscommerce.messaging.payload.OrderConfirmationItem;
import com.dscommerce.messaging.payload.OrderConfirmationMessage;
import org.springframework.stereotype.Component;

@Component
public class OrderCreatedEventMapper {

    public OrderConfirmationMessage toMessage(OrderCreatedEvent event) {
        return new OrderConfirmationMessage(
                event.orderId(),
                event.clientName(),
                event.clientEmail(),
                event.moment(),
                event.status(),
                event.orderItems().stream().map(item -> new OrderConfirmationItem(item.productName(), item.quantity(), item.price())).toList(),
                event.total()
        );
    }
}