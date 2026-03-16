package com.dscommerce.services;

import com.dscommerce.dto.EmailDTO;
import com.dscommerce.dto.OrderDTO;
import com.dscommerce.dto.OrderItemDTO;
import com.dscommerce.entities.*;
import com.dscommerce.entities.enums.OrderStatus;
import com.dscommerce.repositories.OrderItemRepository;
import com.dscommerce.repositories.OrderRepository;
import com.dscommerce.repositories.ProductRepository;
import com.dscommerce.services.exceptions.DatabaseException;
import com.dscommerce.services.exceptions.ResourceNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
public class OrderService {

    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private AuthService authService;

    @Autowired
    private EmailService emailService;

    @Value("${email.from-address}")
    private String fromAddress;

    @Value("${email.from-name}")
    private String fromName;

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

        emailService.plainTextEmail(buildOrderConfirmationEmail(user, order));
        return new OrderDTO(order);
    }

    private EmailDTO buildOrderConfirmationEmail(User user, Order order) {

        double total = order.getItems().stream()
                .mapToDouble(item -> item.getPrice() * item.getQuantity()).sum();

        StringBuilder rows = new StringBuilder();
        for (OrderItem item : order.getItems()) {
            double subtotal = item.getPrice() * item.getQuantity();
            rows.append(String.format(
                    "<tr><td>%s</td><td>%d</td><td>$ %.2f</td><td>$ %.2f</td></tr>",
                    item.getProduct().getName(),
                    item.getQuantity(),
                    item.getPrice(),
                    subtotal
            ));
        }

        String subject = "Order confirmed #" + order.getId();
        String body = String.format("""                                                                                                                                    
      <h2>Order Confirmation #%d</h2>
      <p>Hello, <strong>%s</strong>!</p>
      <p>Received on: <strong>%s</strong></p>
      <p>Status: <strong>%s</strong></p>
  
        <table border="1" cellpadding="8" cellspacing="0">
        <thead>
          <tr>
            <th>Product</th><th>Qty</th><th>Unit Price</th><th>Subtotal</th>
          </tr>
        </thead>
        <tbody>%s</tbody>
      </table>
  
      <p><strong>Total: $ %.2f</strong></p>
      <p>Thank you for your purchase!</p>
      """, order.getId(), user.getName(), order.getMoment(), order.getStatus(), rows, total);

        return new EmailDTO(
                fromAddress,
                fromName,
                user.getEmail(),
                user.getEmail(),
                subject,
                body,
                "text/html"
        );
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
