package com.dscommerce.services.email;

import com.dscommerce.dto.EmailDTO;
import com.dscommerce.entities.User;
import com.dscommerce.entities.enums.OrderStatus;
import com.dscommerce.messaging.payload.OrderConfirmationItem;
import com.dscommerce.messaging.payload.OrderConfirmationMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

@Component
public class EmailFactory {

    @Value("${email.from-address}")
    private String fromAddress;

    @Value("${email.from-name}")
    private String fromName;

    public EmailDTO buildOrderConfirmationEmail(OrderConfirmationMessage message) {

        StringBuilder rows = new StringBuilder();
        for (OrderConfirmationItem item : message.orderItems()) {
            double subtotal = item.price() * item.quantity();
            rows.append(String.format(
                    "<tr><td>%s</td><td>%d</td><td>$ %.2f</td><td>$ %.2f</td></tr>",
                    item.productName(),
                    item.quantity(),
                    item.price(),
                    subtotal
            ));
        }

        String subject = "Order confirmed #" + message.orderId();
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
      """, message.orderId(), message.clientName(), message.moment(), message.status(), rows, message.total());

        return new EmailDTO(
                "DSCommerce <" + fromAddress +">",
                fromName,
                null,
                message.clientEmail(),
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
