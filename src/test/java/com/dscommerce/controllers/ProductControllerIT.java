package com.dscommerce.controllers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.dscommerce.dto.ProductDTO;
import com.dscommerce.tests.ProductFactory;
import com.dscommerce.tests.TokenUtil;
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

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class ProductControllerIT {

    private String username, password, bearerToken, bearerTokenInvalid;
    private Long existingId, nonExistingId, dependentId, countTotalProducts;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TokenUtil tokenUtil;

    @BeforeEach
    void setUp() throws Exception {
        existingId = 2L;
        dependentId = 1L;
        nonExistingId = 100L;
        countTotalProducts = 25L;

        username = "matcamp1981@gmail.com";
        password = "123456";

        bearerToken = tokenUtil.obtainAccessToken(mockMvc, username, password);
        bearerTokenInvalid = bearerToken + "zyx";
    }

    @Test
    public void findAllShouldReturnProductMinDTOWhenProductExist() throws Exception {
        ResultActions result = mockMvc
                .perform(get("/products?size=12&page=0&sort=name&name=")
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.page.totalElements").value(countTotalProducts));
        result.andExpect(jsonPath("$.content").exists());
        result.andExpect(jsonPath("$.content[0].name").value("Macbook Pro"));
        result.andExpect(jsonPath("$.content[0].price").value(1250.0));
        result.andExpect(jsonPath("$.content[1].name").value("PC Gamer"));
        result.andExpect(jsonPath("$.content[1].price").value(1200.0));
        result.andExpect(jsonPath("$.content[2].name").value("PC Gamer Alfa"));
        result.andExpect(jsonPath("$.content[2].price").value(1850.0));
    }

    @Test
    public void findByIdShouldReturnProductDTOWhenIdExist() throws Exception {
        ProductDTO productDTO = ProductFactory.createProductDTO();
        String jsonBody = objectMapper.writeValueAsString(productDTO);

        ResultActions result = mockMvc
                .perform(get("/products/{id}", existingId)
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.id").value(existingId));
        result.andExpect(jsonPath("$.name").value("Smart TV"));
        result.andExpect(jsonPath("$.price").value(2190.0));
        result.andExpect(jsonPath("$.imgUrl").value("https://raw.githubusercontent.com/devsuperior/dscatalog-resources/master/backend/img/2-big.jpg"));
    }

    @Test
    public void findByIdShouldReturnResourceNotFoundWhenIdDoesNotExist() throws Exception {
        ProductDTO productDTO = ProductFactory.createProductDTO();
        String jsonBody = objectMapper.writeValueAsString(productDTO);

        ResultActions result = mockMvc
                .perform(get("/products/{id}", nonExistingId)
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isNotFound());
    }

    @Test
    public void insertShouldReturnNewProductDTOWhenDataIsValid() throws Exception {
        ProductDTO productDTO = ProductFactory.createProductDTO();
        // Dados explícitos intencionalmente para legibilidade do cenário de teste
        productDTO.setName("Flash driver 2TB");
        productDTO.setDescription("A flash drive is a small, portable, and durable data storage");
        productDTO.setPrice(25.90);
        productDTO.setImgUrl("https://www.walmart.com/c/kp/2tb-usb-flash-drive-drives");
        String jsonBody = objectMapper.writeValueAsString(productDTO);

        ResultActions result = mockMvc
                .perform(post("/products")
                        .header("Authorization", "Bearer " + bearerToken)
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isCreated());
        result.andExpect(jsonPath("$.id").exists());
        result.andExpect(jsonPath("$.name").value("Flash driver 2TB"));
        result.andExpect(jsonPath("$.description").value("A flash drive is a small, portable, and durable data storage"));
        result.andExpect(jsonPath("$.price").value(25.90));
        result.andExpect(jsonPath("$.imgUrl").value("https://www.walmart.com/c/kp/2tb-usb-flash-drive-drives"));
    }

    @Test
    public void insertShouldReturnUnauthorizedWhenDataIsNotValid() throws Exception {
        ProductDTO productDTO = ProductFactory.createProductDTO();
        // Dados explícitos intencionalmente para legibilidade do cenário de teste
        productDTO.setName("Flash driver 2TB");
        productDTO.setDescription("A flash drive is a small, portable, and durable data storage");
        productDTO.setPrice(25.90);
        productDTO.setImgUrl("https://www.walmart.com/c/kp/2tb-usb-flash-drive-drives");
        String jsonBody = objectMapper.writeValueAsString(productDTO);

        ResultActions result = mockMvc
                .perform(post("/products")
                        .header("Authorization", "Bearer " + bearerTokenInvalid)
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isUnauthorized());
    }

    @Test
    public void insertShouldReturnUnprocessableEntityWhenNameIsEmpty() throws Exception {
        ProductDTO productDTO = ProductFactory.createProductDTO();
        // Dados explícitos intencionalmente para legibilidade do cenário de teste
        productDTO.setName(" ");
        productDTO.setDescription("A flash drive is a small, portable, and durable data storage");
        productDTO.setPrice(25.90);
        productDTO.setImgUrl("https://www.walmart.com/c/kp/2tb-usb-flash-drive-drives");
        String jsonBody = objectMapper.writeValueAsString(productDTO);

        ResultActions result = mockMvc
                .perform(post("/products")
                        .header("Authorization", "Bearer " + bearerToken)
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isUnprocessableEntity());
    }

    @Test
    public void insertShouldReturnUnprocessableEntityWhenDescriptionIsEmpty() throws Exception {
        ProductDTO productDTO = ProductFactory.createProductDTO();
        // Dados explícitos intencionalmente para legibilidade do cenário de teste
        productDTO.setName("Flash driver 2TB");
        productDTO.setDescription(" ");
        productDTO.setPrice(25.90);
        productDTO.setImgUrl("https://www.walmart.com/c/kp/2tb-usb-flash-drive-drives");
        String jsonBody = objectMapper.writeValueAsString(productDTO);

        ResultActions result = mockMvc
                .perform(post("/products")
                        .header("Authorization", "Bearer " + bearerToken)
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isUnprocessableEntity());
    }

    @Test
    public void insertShouldReturnUnprocessableEntityWhenPriceIsNegative() throws Exception {
        ProductDTO productDTO = ProductFactory.createProductDTO();
        // Dados explícitos intencionalmente para legibilidade do cenário de teste
        productDTO.setName("Flash driver 2TB");
        productDTO.setDescription("A flash drive is a small, portable, and durable data storage");
        productDTO.setPrice(-29.0);
        productDTO.setImgUrl("https://www.walmart.com/c/kp/2tb-usb-flash-drive-drives");
        String jsonBody = objectMapper.writeValueAsString(productDTO);

        ResultActions result = mockMvc
                .perform(post("/products")
                        .header("Authorization", "Bearer " + bearerToken)
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isUnprocessableEntity());
    }

    @Test
    public void insertShouldReturnUnprocessableEntityWhenImgUrlIsEmpty() throws Exception {
        ProductDTO productDTO = ProductFactory.createProductDTO();
        // Dados explícitos intencionalmente para legibilidade do cenário de teste
        productDTO.setName("Flash driver 2TB");
        productDTO.setDescription("A flash drive is a small, portable, and durable data storage");
        productDTO.setPrice(25.90);
        productDTO.setImgUrl(" ");
        String jsonBody = objectMapper.writeValueAsString(productDTO);

        ResultActions result = mockMvc
                .perform(post("/products")
                        .header("Authorization", "Bearer " + bearerToken)
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isUnprocessableEntity());
    }

    @Test
    public void updateShouldReturnProductDTOWhenDataIsValid() throws Exception {
        ProductDTO productDTO = ProductFactory.updateProductDTO();
        String jsonBody = objectMapper.writeValueAsString(productDTO);

        ResultActions result = mockMvc
                .perform(put("/products/{id}", existingId)
                        .header("Authorization", "Bearer " + bearerToken)
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.id").value(2L));
        result.andExpect(jsonPath("$.name").value("PC Gamer ZZ"));
        result.andExpect(jsonPath("$.description").value("Lorem ipsum dolor sit amet, consectetur adipiscing elit"));
        result.andExpect(jsonPath("$.price").value(1400.0));
    }

    @Test
    public void updateShouldReturnMethodNotAllowedWhenIdIsEmpty() throws Exception {
        ProductDTO productDTO = ProductFactory.updateProductDTO();
        String jsonBody = objectMapper.writeValueAsString(productDTO);

        ResultActions result = mockMvc
                .perform(put("/products", existingId)
                        .header("Authorization", "Bearer " + bearerToken)
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isMethodNotAllowed());
    }

    @Test
    public void updateShouldReturnUnauthorizedWhenUserIsNotAuthenticate() throws Exception {
        ProductDTO productDTO = ProductFactory.updateProductDTO();
        String jsonBody = objectMapper.writeValueAsString(productDTO);

        ResultActions result = mockMvc
                .perform(put("/products/{id}", existingId)
                        .header("Authorization", "Bearer " + bearerTokenInvalid)
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isUnauthorized());
    }

    @Test
    public void updateShouldReturnNotFoundWhenIdDoesNotExist() throws Exception {
        ProductDTO productDTO = ProductFactory.updateProductDTO();
        String jsonBody = objectMapper.writeValueAsString(productDTO);

        ResultActions result = mockMvc
                .perform(put("/products/{id}", nonExistingId)
                        .header("Authorization", "Bearer " + bearerToken)
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isNotFound());
    }

    @Test
    public void deleteShouldReturnNoContentWhenIdIsValid() throws Exception {
        ResultActions result = mockMvc
                .perform(delete("/products/{id}", existingId)
                        .header("Authorization", "Bearer " + bearerToken));

        result.andExpect(status().isNoContent());
    }

    @Test
    public void deleteShouldReturnNotFoundWhenIdDoesNotExist() throws Exception {
        ResultActions result = mockMvc
                .perform(delete("/products/{id}", nonExistingId)
                        .header("Authorization", "Bearer " + bearerToken));

        result.andExpect(status().isNotFound());
    }

    @Test
    public void deleteShouldReturnBadRequestWhenIdIsDependent() throws Exception {
        ResultActions result = mockMvc
                .perform(delete("/products/{id}", dependentId)
                        .header("Authorization", "Bearer " + bearerToken));

        result.andExpect(status().isBadRequest());
    }

}
