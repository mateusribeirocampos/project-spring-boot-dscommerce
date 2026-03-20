package com.dscommerce.tests;

import com.dscommerce.dto.UserDTO;
import com.dscommerce.dto.UserInsertDTO;
import com.dscommerce.dto.UserUpdateDTO;
import com.dscommerce.entities.Order;
import com.dscommerce.entities.Payment;
import com.dscommerce.entities.User;
import com.dscommerce.entities.enums.OrderStatus;
import com.dscommerce.projections.UserDetailsProjection;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

public class UserFactory {

    public static User createUser() {
        User user = new User(3L, "Maria Brown", "maria@gmail.com", "988888888", LocalDate.parse("1983-07-25"), "12345678");
        user.getOrders().add(createOrder());
        return user;
    }

    public static UserDTO createUserDTO() {
        return new UserDTO(3L, "Maria Brown", "maria@gmail.com", "988888888", LocalDate.parse("1983-07-25"));
    }

    public static UserInsertDTO createUserInsertDTO() {
        UserInsertDTO dto = new UserInsertDTO();
        dto.setPassword("12345678");
        return dto;
    }

    public static Order createOrder() {
        return new Order(3L, Instant.now(), OrderStatus.WAITING_PAYMENT, new User(), new Payment());
    }

    public static class UserDetailsProjectionImpl implements UserDetailsProjection {

        private String username;
        private String password;
        private Long roleId;
        private String authority;

        public UserDetailsProjectionImpl(String username, String password, Long roleId, String authority) {
            this.username = username;
            this.password = password;
            this.roleId = roleId;
            this.authority = authority;
        }

        @Override
        public String getUsername() {
            return username;
        }

        @Override
        public String getPassword() {
            return password;
        }

        @Override
        public Long getRoleId() {
            return roleId;
        }

        @Override
        public String getAuthority() {
            return authority;
        }
    }

    public static List<UserDetailsProjection> createUserDetailsProjectionList() {
        return List.of(new UserDetailsProjectionImpl("maria@gmail.com", "12345678", 1L, "ROLE_CLIENT"));
    }
}
