package com.dscommerce.tests;

import com.dscommerce.dto.UserDTO;
import com.dscommerce.entities.Order;
import com.dscommerce.entities.Payment;
import com.dscommerce.entities.User;
import com.dscommerce.entities.enums.OrderStatus;

import java.time.Instant;
import java.time.LocalDate;

public class UserFactory {

    public static User createUser() {
        User user = new User(3L, "Rodrigo Blue", "rodrigo@gmail.com", "988888888", LocalDate.parse("1983-07-25"), "12345678");
        user.getOrders().add(createOrder());
        return user;
    }

    public static Order createOrder() {
        return new Order(3L, Instant.now(), OrderStatus.WAITING_PAYMENT, new User(), new Payment());
    }
}
