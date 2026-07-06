package com.dscommerce.repositories;

import com.dscommerce.entities.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query("SELECT obj " +
            "FROM Order obj " +
            "JOIN obj.client u " +
            "WHERE UPPER(u.name) LIKE UPPER(CONCAT('%', :clientName, '%'))")
    Page<Order> searchByClientName(String clientName, Pageable pageable);
}
