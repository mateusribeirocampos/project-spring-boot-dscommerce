package com.dscommerce.tests;

import com.dscommerce.dto.OrderDTO;
import com.dscommerce.dto.OrderItemDTO;
import com.dscommerce.dto.OrderSummaryDTO;
import com.dscommerce.entities.Order;
import com.dscommerce.entities.Payment;
import com.dscommerce.entities.User;
import com.dscommerce.entities.enums.OrderStatus;

import java.time.Instant;

public class OrderFactory {

    public static Order createOrder() {
        User client = UserFactory.createUser();
        client.setId(1L);
        return new Order(3L, Instant.now(), OrderStatus.WAITING_PAYMENT, client, new Payment());
    }

    public static OrderDTO createOrderDTO() {
        OrderDTO orderDTO = new OrderDTO(createOrder());
        OrderItemDTO itemDTO = new OrderItemDTO(1L, "Flash Drive 1TB", 100.0, 1, "https://m.media-amazon.com/images/I/71eKXcpG0vL._AC_SX425_.jpg");
        orderDTO.getItems().add(itemDTO);
        return orderDTO;
    }

    public static OrderSummaryDTO createOrderSummaryDTO() {
        Order order = createOrder();
        return new OrderSummaryDTO(order);
    }
}
