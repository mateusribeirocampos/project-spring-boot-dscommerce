package com.dscommerce.dto;

import com.dscommerce.entities.Order;
import com.dscommerce.entities.enums.OrderStatus;
import jakarta.validation.constraints.NotEmpty;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class OrderDTO {

    private Long id;
    private Instant moment;
    private OrderStatus orderStatus;
    private ClientDTO client;
    private PaymentDTO payment;

    @NotEmpty(message = "Order must have at least one item")
    private List<OrderItemDTO> items = new ArrayList<>();

    public OrderDTO() {}

    public OrderDTO(Long id, Instant moment, OrderStatus orderStatus, ClientDTO client, PaymentDTO payment) {
        this.id = id;
        this.moment = moment;
        this.orderStatus = orderStatus;
        this.client = client;
        this.payment = payment;
    }

    public OrderDTO(Order entity) {
        id = entity.getId();
        moment = entity.getMoment();
        orderStatus = entity.getOrderStatus();
        client = new ClientDTO(entity.getClient());
        if (entity.getPayment() == null) {
            payment = new PaymentDTO(entity.getPayment());
        }
    }

    public Long getId() {
        return id;
    }

    public Instant getMoment() {
        return moment;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public ClientDTO getClient() {
        return client;
    }

    public PaymentDTO getPayment() {
        return payment;
    }

    public Double getTotal() {
        double sum = 0.0;
        for (OrderItemDTO item : items) {
            sum += item.getSubTotal();
        }
        return sum;
    }
}
