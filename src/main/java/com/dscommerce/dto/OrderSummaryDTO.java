package com.dscommerce.dto;

import com.dscommerce.entities.Order;
import com.dscommerce.entities.enums.OrderStatus;

import java.time.Instant;
import java.math.BigDecimal;

public class OrderSummaryDTO {

    private Long id;
    private Instant moment;
    private OrderStatus orderStatus;
    private String clientName;
    private BigDecimal total;

    public OrderSummaryDTO() {
    }

    public OrderSummaryDTO(Long id, Instant moment, OrderStatus orderStatus, String clientName, BigDecimal total) {
        this.id = id;
        this.moment = moment;
        this.orderStatus = orderStatus;
        this.clientName = clientName;
        this.total = total;
    }

    public OrderSummaryDTO(Order entity) {
        id = entity.getId();
        moment = entity.getMoment();
        orderStatus = entity.getStatus();
        clientName = entity.getClient().getName();
        total = entity.getItems().stream()
                .map(i -> i.getPrice().multiply(BigDecimal.valueOf(i.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getMoment() {
        return moment;
    }

    public void setMoment(Instant moment) {
        this.moment = moment;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }
}