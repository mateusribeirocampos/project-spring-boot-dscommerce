package com.dscommerce.messaging.mapper;

import com.dscommerce.entities.enums.OrderStatus;
import com.dscommerce.messaging.event.OrderCreatedEvent;
import com.dscommerce.messaging.payload.OrderConfirmationMessage;
import com.dscommerce.tests.OrderCreatedEventFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

public class OrderCreatedEventMapperTests {

    private OrderCreatedEventMapper orderCreatedEventMapper;

    @BeforeEach
    void setUp() {
        orderCreatedEventMapper = new OrderCreatedEventMapper();
    }

    @Test
    public void shouldCopyOrderIdFromEventToMessage() {
        OrderCreatedEvent event = OrderCreatedEventFactory.orderCreatedEvent();
        OrderConfirmationMessage message = orderCreatedEventMapper.toMessage(event);

        Assertions.assertEquals(event.orderId(), message.orderId());
    }

    @Test
    public void shouldCopyClientNameFromEventToMessage() {
        OrderCreatedEvent event = OrderCreatedEventFactory.orderCreatedEvent();
        OrderConfirmationMessage message = orderCreatedEventMapper.toMessage(event);

        Assertions.assertEquals(event.clientName(), message.clientName());
    }

    @Test
    public void shouldCopyClientEmailFromEventToMessage() {
        OrderCreatedEvent event = OrderCreatedEventFactory.orderCreatedEvent();
        OrderConfirmationMessage message = orderCreatedEventMapper.toMessage(event);

        Assertions.assertEquals(event.clientEmail(), message.clientEmail());
    }

    @Test
    public void shouldCopyOrderItemFromEventToMessage() {
        OrderCreatedEvent event = OrderCreatedEventFactory.orderCreatedEvent();
        OrderConfirmationMessage message = orderCreatedEventMapper.toMessage(event);

        Assertions.assertEquals(event.orderItems().size(), message.orderItems().size());
        Assertions.assertEquals(event.orderItems().getFirst().productName(), message.orderItems().getFirst().productName());
        Assertions.assertEquals(event.orderItems().getFirst().quantity(), message.orderItems().getFirst().quantity());
        Assertions.assertEquals(event.orderItems().getFirst().price(), message.orderItems().getFirst().price());
    }

    @Test
    public void shouldCopyMomentFromEventToMessage() {
        OrderCreatedEvent event = OrderCreatedEventFactory.orderCreatedEvent();
        OrderConfirmationMessage message = orderCreatedEventMapper.toMessage(event);

        Assertions.assertEquals(event.moment(), message.moment());
    }

    @Test
    public void shouldCopyTotalFromEventToMessage() {
        OrderCreatedEvent event = OrderCreatedEventFactory.orderCreatedEvent();
        OrderConfirmationMessage message = orderCreatedEventMapper.toMessage(event);

        Assertions.assertEquals(event.total(), message.total());
    }

    @ParameterizedTest
    @EnumSource(OrderStatus.class)
    public void shouldCopyStatusFromEventToMessage(OrderStatus status) {
        OrderCreatedEvent event = OrderCreatedEventFactory.orderCreatedEvent(status);
        OrderConfirmationMessage message = orderCreatedEventMapper.toMessage(event);

        Assertions.assertEquals(status.name(), message.status().name());
    }
}
