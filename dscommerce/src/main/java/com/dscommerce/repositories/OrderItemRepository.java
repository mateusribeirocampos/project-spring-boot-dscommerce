package com.dscommerce.repositories;

import com.dscommerce.entities.OrderItem;
import com.dscommerce.entities.pk.OrderItemPk;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, OrderItemPk> {
}