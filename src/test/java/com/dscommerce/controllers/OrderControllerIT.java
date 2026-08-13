package com.dscommerce.controllers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.dscommerce.dto.OrderDTO;
import com.dscommerce.dto.OrderItemDTO;
import com.dscommerce.tests.OrderFactory;
import com.dscommerce.tests.TokenUtil;
import com.dscommerce.testsupport.AbstractIntegrationTest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class OrderControllerIT extends AbstractIntegrationTest {

    private String usernameAdmin, usernameClient, password, bearerTokenAdmin, bearerTokenClient, bearerTokenInvalid;
    private Long existingId, nonExistingId, otherOrderId, orderId, countTotalOrders;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TokenUtil tokenUtil;

    @BeforeEach
    void setup() throws Exception {
        existingId = 1L;
        otherOrderId = 2L;
        orderId = 1L;
        nonExistingId = 999L;
        countTotalOrders = 3L;

        usernameAdmin = "matcamp1981@gmail.com";
        usernameClient = "maria@gmail.com";

        password = "123456";

        bearerTokenAdmin = tokenUtil.obtainAccessToken(mockMvc, usernameAdmin, password);
        bearerTokenClient = tokenUtil.obtainAccessToken(mockMvc, usernameClient, password);
        bearerTokenInvalid = bearerTokenClient + "zyx";
    }

    @Test
    public void findAllShouldReturnAllOrderWhenUserIsAdminAndIsAuthenticated() throws Exception {
        ResultActions result =
                mockMvc.perform(get("/orders?size=21&page=0&sort=moment,asc&clientName=")
                        .header("Authorization", "Bearer " + bearerTokenAdmin)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.content").exists())
                .andExpect(jsonPath("$.page.totalElements").value(countTotalOrders))
                .andExpect(jsonPath("$.content[0].id").value(existingId))
                .andExpect(jsonPath("$.content[0].moment").exists())
                .andExpect(jsonPath("$.content[0].orderStatus").value("PAID"))
                .andExpect(jsonPath("$.content[0].clientName").value("Maria Brown"))
                .andExpect(jsonPath("$.content[0].total").value(1431.0));
    }

    @Test
    public void findAllShouldReturnAllUnauthorizedWhenUserDoesNotAuthenticated() throws Exception {
        ResultActions result =
                mockMvc.perform(get("/orders?size=21&page=0&sort=moment,asc&clientName=")
                        .header("Authorization", "Bearer " + bearerTokenInvalid)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isUnauthorized());
    }

    @Test
    public void findAllShouldReturnAllForbiddenWhenUserIsNotAdmin() throws Exception {
        ResultActions result =
                mockMvc.perform(get("/orders?size=21&page=0&sort=moment,asc&clientName=")
                        .header("Authorization", "Bearer " + bearerTokenClient)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isForbidden());
    }

    @Test
    public void findByIdShouldReturnOrderByIdWhenUserAdminIsAuthenticated() throws Exception {
        ResultActions result =
                mockMvc.perform(get("/orders/{id}", existingId)
                        .header("Authorization", "Bearer " + bearerTokenAdmin)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(orderId))
                .andExpect(jsonPath("$.moment").exists())
                .andExpect(jsonPath("$.orderStatus").value("PAID"))
                .andExpect(jsonPath("$.client.id").value(existingId))
                .andExpect(jsonPath("$.client.name").value("Maria Brown"))
                .andExpect(jsonPath("$.payment.id").value(1L))
                .andExpect(jsonPath("$.payment.moment").exists())
                .andExpect(jsonPath("$.items[0].productId").value(1L))
                .andExpect(jsonPath("$.items[0].name").value("The Lord of the Rings"))
                .andExpect(jsonPath("$.items[0].price").value(90.5))
                .andExpect(jsonPath("$.items[0].quantity").value(2L))
                .andExpect(jsonPath("$.items[0].imgUrl").value("https://raw.githubusercontent.com/devsuperior/dscatalog-resources/master/backend/img/1-big.jpg"))
                .andExpect(jsonPath("$.items[0].subTotal").value(181.0));
    }

    @Test
    public void findByIdShouldReturnOrderByIdWhenUserClientIsAuthenticated() throws Exception {
        ResultActions result =
                mockMvc.perform(get("/orders/{id}", existingId)
                        .header("Authorization", "Bearer " + bearerTokenClient)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(orderId))
                .andExpect(jsonPath("$.moment").exists())
                .andExpect(jsonPath("$.orderStatus").value("PAID"))
                .andExpect(jsonPath("$.client.id").value(existingId))
                .andExpect(jsonPath("$.client.name").value("Maria Brown"))
                .andExpect(jsonPath("$.payment.id").value(1L))
                .andExpect(jsonPath("$.payment.moment").exists())
                .andExpect(jsonPath("$.items[0].productId").value(1L))
                .andExpect(jsonPath("$.items[0].name").value("The Lord of the Rings"))
                .andExpect(jsonPath("$.items[0].price").value(90.5))
                .andExpect(jsonPath("$.items[0].quantity").value(2L))
                .andExpect(jsonPath("$.items[0].imgUrl").value("https://raw.githubusercontent.com/devsuperior/dscatalog-resources/master/backend/img/1-big.jpg"))
                .andExpect(jsonPath("$.items[0].subTotal").value(181.0));
    }

    @Test
    public void findByIdShouldReturnForbiddenWhenUserIsClientAuthenticated() throws Exception {
        ResultActions result =
                mockMvc.perform(get("/orders/{id}", otherOrderId)
                        .header("Authorization", "Bearer " + bearerTokenClient)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isForbidden());
    }

    @Test
    public void findByIdShouldReturnUnauthorizedWhenUserIsNotAuthenticated() throws Exception {
        ResultActions result =
                mockMvc.perform(get("/orders/{id}", existingId)
                        .header("Authorization", "Bearer " + bearerTokenInvalid)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isUnauthorized());
    }

    @Test
    public void findByIdShouldReturnNotFoundWhenUserIsNotAdminAuthenticated() throws Exception {
        ResultActions result =
                mockMvc.perform(get("/orders/{id}", nonExistingId)
                        .header("Authorization", "Bearer " + bearerTokenClient)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isNotFound());
    }

    @Test
    public void insertShouldReturnNewOrderWhenUserIsAuthenticated() throws Exception {
        OrderDTO orderDTO = OrderFactory.createOrderDTO();
        orderDTO.setItems(List.of(new OrderItemDTO(1L, "The Lord of the Rings", 90.5, 2,
                "https://raw.githubusercontent.com/devsuperior/dscatalog-resources/master/backend/img/1-big.jpg")));

        String jsonBody = objectMapper.writeValueAsString(orderDTO);

        ResultActions result =
                mockMvc.perform(post("/orders")
                        .header("Authorization", "Bearer " + bearerTokenClient)
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(4L))
                .andExpect(jsonPath("$.moment").exists())
                .andExpect(jsonPath("$.orderStatus").value("WAITING_PAYMENT"))
                .andExpect(jsonPath("$.client.id").value(1L))
                .andExpect(jsonPath("$.client.name").value("Maria Brown"))
                .andExpect(jsonPath("$.payment").doesNotExist())
                .andExpect(jsonPath("$.items[0].productId").value(1L))
                .andExpect(jsonPath("$.items[0].name").value("The Lord of the Rings"))
                .andExpect(jsonPath("$.items[0].price").value(90.5))
                .andExpect(jsonPath("$.items[0].quantity").value(2))
                .andExpect(jsonPath("$.items[0].imgUrl").exists())
                .andExpect(jsonPath("$.items[0].subTotal").value(181.0));
    }

    @Test
    public void insertShouldReturnUnauthorizedWhenUserIsNotAuthenticated() throws Exception {
        ResultActions result =
                mockMvc.perform(post("/orders")
                        .header("Authorization", "Bearer " + bearerTokenInvalid)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isUnauthorized());
    }

    @Test
    public void updateShouldReturnOrderDTOUpdatedWhenAdminIsAuthenticated() throws Exception {
        OrderDTO orderDTO = OrderFactory.createOrderDTO();
        orderDTO.setItems(List.of(new OrderItemDTO(1L, "The Lord of the Rings", 90.5, 2,
                "https://raw.githubusercontent.com/devsuperior/dscatalog-resources/master/backend/img/1-big.jpg")));

        String jsonBody = objectMapper.writeValueAsString(orderDTO);

        ResultActions result =
                mockMvc.perform(put("/orders/{id}", existingId)
                        .header("Authorization", "Bearer " + bearerTokenAdmin)
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(orderId))
                .andExpect(jsonPath("$.moment").exists())
                .andExpect(jsonPath("$.orderStatus").value("PAID"))
                .andExpect(jsonPath("$.client.id").value(2L))
                .andExpect(jsonPath("$.client.name").value("Alex Green"))
                .andExpect(jsonPath("$.payment.id").value(1L))
                .andExpect(jsonPath("$.payment.moment").exists())
                .andExpect(jsonPath("$.items.length()").value(2));
    }

    @Test
    public void updateShouldReturnUnauthorizedWhenUserIsNotAuthenticated() throws Exception {
        OrderDTO orderDTO = OrderFactory.createOrderDTO();
        orderDTO.setItems(List.of(new OrderItemDTO(1L, "The Lord of the Rings", 90.5, 2,
                "https://raw.githubusercontent.com/devsuperior/dscatalog-resources/master/backend/img/1-big.jpg")));

        String jsonBody = objectMapper.writeValueAsString(orderDTO);

        ResultActions result =
                mockMvc.perform(put("/orders/{id}", existingId)
                        .header("Authorization", "Bearer " + bearerTokenInvalid)
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isUnauthorized());
    }

    @Test
    public void updateShouldReturnForbiddenWhenUserIsNotAdmin() throws Exception {
        OrderDTO orderDTO = OrderFactory.createOrderDTO();
        orderDTO.setItems(List.of(new OrderItemDTO(1L, "The Lord of the Rings", 90.5, 2,
                "https://raw.githubusercontent.com/devsuperior/dscatalog-resources/master/backend/img/1-big.jpg")));

        String jsonBody = objectMapper.writeValueAsString(orderDTO);

        ResultActions result =
                mockMvc.perform(put("/orders/{id}", existingId)
                        .header("Authorization", "Bearer " + bearerTokenClient)
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isForbidden());
    }

    @Test
    public void deleteShouldReturnNoContentWhenOrderIdIsValidAndAdminAuthenticated() throws Exception {
        ResultActions result =
                mockMvc.perform(delete("/orders/{id}", existingId)
                        .header("Authorization", "Bearer " + bearerTokenAdmin));

        result.andExpect(status().isNoContent());
    }
    @Test
    public void  deleteShouldReturnNotFoundWhenOrderIdDoesNotExist() throws Exception {
        ResultActions result =
                mockMvc.perform(delete("/orders/{id}", nonExistingId)
                        .header("Authorization", "Bearer " + bearerTokenAdmin));

        result.andExpect(status().isNotFound());
    }
    @Test
    public void deleteShouldReturnForbiddenWhenUserIsNotAdmin() throws Exception {
        ResultActions result =
                mockMvc.perform(delete("/orders/{id}", existingId)
                        .header("Authorization", "Bearer " + bearerTokenClient));

        result.andExpect(status().isForbidden());
    }

    @Test
    public void deleteShouldReturnUnauthorizedWhenUserIsNotAuthenticated() throws Exception {
        ResultActions result =
                mockMvc.perform(delete("/orders/{id}", existingId)
                        .header("Authorization", "Bearer " + bearerTokenInvalid));

        result.andExpect(status().isUnauthorized());
    }
}

