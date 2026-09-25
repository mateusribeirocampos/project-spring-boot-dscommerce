package com.dscommerce.messaging.mapper;

import com.dscommerce.entities.enums.OrderStatus;
import com.dscommerce.messaging.event.OrderCreatedEvent;
import com.dscommerce.messaging.payload.OrderConfirmationItem;
import com.dscommerce.messaging.payload.OrderConfirmationMessage;
import com.dscommerce.messaging.payload.OrderConfirmationStatus;
import org.springframework.stereotype.Component;

@Component
public class OrderCreatedEventMapper {

    public OrderConfirmationMessage toMessage(OrderCreatedEvent event) {
        return new OrderConfirmationMessage(
                event.orderId(),
                event.clientName(),
                event.clientEmail(),
                event.moment(),
                toConfirmationStatus(event.status()),
                event.orderItems().stream().map(item -> new OrderConfirmationItem(item.productName(), item.quantity(), item.price())).toList(),
                event.total()
        );
    }

    private OrderConfirmationStatus toConfirmationStatus(OrderStatus orderStatus) {
        return switch (orderStatus) {
            case DELIVERED -> OrderConfirmationStatus.DELIVERED;
            case CANCELED -> OrderConfirmationStatus.CANCELED;
            case WAITING_PAYMENT -> OrderConfirmationStatus.WAITING_PAYMENT;
            case SHIPPED -> OrderConfirmationStatus.SHIPPED;
            case PAID -> OrderConfirmationStatus.PAID;
        };
    }
}