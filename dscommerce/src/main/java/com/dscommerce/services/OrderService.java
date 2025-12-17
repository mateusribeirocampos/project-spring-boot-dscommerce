package com.dscommerce.services;

import com.dscommerce.dto.OrderDTO;
import com.dscommerce.dto.OrderItemDTO;
import com.dscommerce.entities.*;
import com.dscommerce.entities.enums.OrderStatus;
import com.dscommerce.repositories.CategoryRepository;
import com.dscommerce.repositories.OrderItemRepository;
import com.dscommerce.repositories.OrderRepository;
import com.dscommerce.repositories.ProductRepository;
import com.dscommerce.services.exceptions.DatabaseException;
import com.dscommerce.services.exceptions.ResourceNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {

    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private AuthService authService;

    @Transactional(readOnly = true)
    public List<OrderDTO> findAll() {
        logger.info("Finding all orders");
        List<Order> orderPage = orderRepository.findAll();
        return orderPage.stream().map(OrderDTO::new).toList();
    }

    @Transactional(readOnly = true)
    public OrderDTO findById(Long id) {
        logger.info("Finding one order by id: {}", id);
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found for id: " + id));
        authService.validateSelfOrAdmin(order.getClient().getId());
        return new OrderDTO(order);
    }

    @Transactional
    public OrderDTO insert(OrderDTO dto) {
        logger.info("Creating a order {}", dto.getClient());
        Order order = new Order();
        order.setMoment(Instant.now());
        order.setStatus(OrderStatus.WAITING_PAYMENT);

        User user = userService.authenticated();
        order.setClient(user);

        for (OrderItemDTO itemDTO : dto.getItems()) {
            Product product = productRepository.getReferenceById(itemDTO.getProductId());
            OrderItem item = new OrderItem(
                    order,
                    product,
                    itemDTO.getQuantity(),
                    product.getPrice());
            order.getItems().add(item);
        }
        orderRepository.save(order);
        orderItemRepository.saveAll(order.getItems());
        return new OrderDTO(order);
    }

    @Transactional
    public OrderDTO update(Long id, OrderDTO dto) {
        logger.info("Updating a order {} by id: {}", dto.getClient(), id);
        try {
            Order order = orderRepository.getReferenceById(id);
            User user = userService.authenticated();
            order.setClient(user);

            for (OrderItemDTO itemDTO : dto.getItems()) {
                Product product = productRepository.getReferenceById(itemDTO.getProductId());
                OrderItem item = new OrderItem(
                        order,
                        product,
                        itemDTO.getQuantity(),
                        product.getPrice());
                order.getItems().add(item);
            }
            orderRepository.save(order);
            orderItemRepository.saveAll(order.getItems());
            return new OrderDTO(order);

        } catch (EntityNotFoundException e) {
            throw new ResourceNotFoundException("Resource not found for id: " + id);
        }
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public void delete(Long id) {
        logger.info("Deleting a order by id: {}", id);
        if (!orderRepository.existsById(id)) {
            throw new ResourceNotFoundException("Resource not found for id: " + id);
        }
        try {
         orderRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new DatabaseException("Referential integrity failure");
        }
    }
}
