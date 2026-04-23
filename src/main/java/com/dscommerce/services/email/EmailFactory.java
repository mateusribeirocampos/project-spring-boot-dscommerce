package com.dscommerce.services.email;

import com.dscommerce.dto.EmailDTO;
import com.dscommerce.entities.Order;
import com.dscommerce.entities.OrderItem;
import com.dscommerce.entities.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class EmailFactory {

    @Value("${email.from-address}")
    private String fromAddress;

    @Value("${email.from-name}")
    private String fromName;

    public EmailDTO buildOrderConfirmationEmail(User user, Order order) {

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
                "DSCommerce <" + fromAddress +">",
                fromName,
                null,
                user.getEmail(),
                subject,
                body,
                "text/html"
        );
    }

    public EmailDTO buildResetTokenEmail(String link, User user) {
        String body = "Olá, " + user.getName() + "!\n\n" +
                "Recebemos uma solicitação para redefinir sua senha.\n\n" +
                "Clique no link abaixo para criar uma nova senha (expira em 10 minutos):\n" +
                link + "\n\n" +
                "Se você não solitiou esse serviço, por favor, ignore este email.\n\n" +
                "Atenciosamente \n" +
                "Equipe técnica da DSCommerce";

        return new EmailDTO(
                "DSCommerce <" + fromAddress +">", // fromEmail
                fromName, // fromName (já no from)
                null, // replyTo
                user.getEmail(), // toEmail
                "DSCommerce - Redefinição de senha", // subject
                body, // body
                "text/plain" // contentType
        );
    }
}
