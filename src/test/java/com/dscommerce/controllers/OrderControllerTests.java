package com.dscommerce.controllers;

import com.dscommerce.dto.OrderDTO;
import com.dscommerce.dto.OrderSummaryDTO;
import com.dscommerce.services.OrderService;
import com.dscommerce.services.exceptions.ResourceNotFoundException;
import com.dscommerce.tests.OrderFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = OrderController.class, excludeAutoConfiguration = {SecurityAutoConfiguration.class})
public class OrderControllerTests {

    private Long existingId, nonExistingId, orderId;
    private OrderDTO orderDTO;
    private PageImpl<OrderSummaryDTO> pageOrderSummaryDTO;
    private OrderSummaryDTO orderSummaryDTO;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private OrderService orderService;

    @BeforeEach
    void setUp() throws Exception {
        orderId = 3L;
        existingId = 1L;
        nonExistingId = 999L;
        orderDTO = OrderFactory.createOrderDTO();
        orderSummaryDTO = OrderFactory.createOrderSummaryDTO();
        pageOrderSummaryDTO = new PageImpl<>(List.of(orderSummaryDTO));
    }

    @Test
    public void findAllShouldReturnOrderDTOWhenOrderExist() throws Exception {
        when(orderService.findAll(any(), any(Pageable.class))).thenReturn(pageOrderSummaryDTO);

        ResultActions result =
                mockMvc.perform(get("/orders?size=21&page=0&sort=moment,asc&clientName=")
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.content").exists())
                .andExpect(jsonPath("$.page.totalElements").value(1))
                .andExpect(jsonPath("$.content[0].id").value(orderId))
                .andExpect(jsonPath("$.content[0].moment").exists())
                .andExpect(jsonPath("$.content[0].orderStatus").value("WAITING_PAYMENT"));
    }

    @Test
    public void findByIdShouldReturnOrderDTOWhenOrderIdExist() throws Exception {
        when(orderService.findById(existingId)).thenReturn(orderDTO);

        ResultActions result =
                mockMvc.perform(get("/orders/{id}", existingId)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(orderId))
                .andExpect(jsonPath("$.moment").exists())
                .andExpect(jsonPath("$.orderStatus").value("WAITING_PAYMENT"));
    }

    @Test
    public void findByIdShouldReturnExceptionWhenOrderIdDoesNotExist() throws Exception {
        when(orderService.findById(nonExistingId)).thenThrow(ResourceNotFoundException.class);

        ResultActions result =
                mockMvc.perform(get("/orders/{id}", nonExistingId)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isNotFound());
    }

    @Test
    public void insertShouldReturnCreateOrderWhenOrderIsValid() throws Exception {
        when(orderService.insert(any())).thenReturn(orderDTO);
        String jsonBody = objectMapper.writeValueAsString(orderDTO);

        ResultActions result =
                mockMvc.perform(post("/orders")
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(orderId))
                .andExpect(jsonPath("$.moment").exists())
                .andExpect(jsonPath("$.orderStatus").value("WAITING_PAYMENT"));
    }

    @Test
    public void insertShouldReturnUnprocessableEntityWhenOrderDoesNotValid() throws Exception {
        OrderDTO orderDTOEmpty = new OrderDTO();
        String jsonBody = objectMapper.writeValueAsString(orderDTOEmpty);

        ResultActions result =
                mockMvc.perform(post("/orders")
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isUnprocessableEntity());
    }

    @Test
    public void updateShouldReturnOrderDTOWhenDataIsValid() throws Exception {
        when(orderService.update(eq(existingId), any())).thenReturn(orderDTO);
        String jsonBody = objectMapper.writeValueAsString(orderDTO);

        ResultActions result =
                mockMvc.perform(put("/orders/{id}", existingId)
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(orderId))
                .andExpect(jsonPath("$.moment").exists())
                .andExpect(jsonPath("$.orderStatus").value("WAITING_PAYMENT"));
    }

    @Test
    public void updateShouldReturnNotFoundWhenDataIdDoesNotValid() throws Exception {
        when(orderService.update(eq(nonExistingId), any())).thenThrow(ResourceNotFoundException.class);
        String jsonBody = objectMapper.writeValueAsString(orderDTO);

        ResultActions result =
                mockMvc.perform(put("/orders/{id}", nonExistingId)
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isNotFound());
    }

    @Test
    public void deleteShouldReturnNoContentWhenIdIsCorrect() throws Exception {
        doNothing().when(orderService).delete(existingId);
        ResultActions result =
                mockMvc.perform(delete("/orders/{id}", existingId)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isNoContent());
    }

    @Test
    public void deleteShouldReturnNotFoundWhenIdDoesNotCorrect() throws Exception {
        doThrow(ResourceNotFoundException.class).when(orderService).delete(nonExistingId);
        ResultActions result =
                mockMvc.perform(delete("/orders/{id}", nonExistingId)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isNotFound());
    }
}
