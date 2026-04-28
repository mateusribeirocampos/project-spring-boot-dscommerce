package com.dscommerce.controllers;

import com.dscommerce.dto.ProductDTO;
import com.dscommerce.dto.ProductMinDTO;
import com.dscommerce.services.ProductService;
import com.dscommerce.services.exceptions.DatabaseException;
import com.dscommerce.services.exceptions.ResourceNotFoundException;
import com.dscommerce.tests.ProductFactory;
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

@WebMvcTest(value = ProductController.class, excludeAutoConfiguration = {SecurityAutoConfiguration.class})
public class ProductControllerTests {

    private ProductDTO productDTO;
    private ProductMinDTO productMinDTO;
    private PageImpl<ProductMinDTO> pageProduct;
    private Long exitingId, nonExistingId, dependentId;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductService productService;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() throws Exception {
        exitingId = 1L;
        dependentId = 2L;
        nonExistingId = 100L;

        productDTO = ProductFactory.createProductDTO();
        productMinDTO = ProductFactory.createProductMinDTO();
        pageProduct = new PageImpl<>(List.of(productMinDTO));
    }

    @Test
    public void findAllShouldReturnProductMinDTOWhenProductExist() throws Exception {
        when(productService.findAll(any(), any(Pageable.class))).thenReturn(pageProduct);

        ResultActions result =
                mockMvc.perform(get("/products?page=0&size=12&sort=name,asc")
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.content").exists())
                .andExpect(jsonPath("$.page.totalElements").value(1))
                .andExpect(jsonPath("$.content[0].id").value(exitingId))
                .andExpect(jsonPath("$.content[0].name").value("PC Gamer WW"));
    }

    @Test
    public void findByIdShouldReturnProductDTOWhenProductIdExist() throws Exception {
        when(productService.findById(exitingId)).thenReturn(productDTO);
        String jsonBody = objectMapper.writeValueAsString(productDTO);

        ResultActions result =
                mockMvc.perform(get("/products/{id}", exitingId)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(exitingId))
                .andExpect(jsonPath("$.name").value("PC Gamer WW"))
                .andExpect(jsonPath("$.description").value("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum."))
                .andExpect(jsonPath("$.price").value(1350.0))
                .andExpect(jsonPath("$.imgUrl").value("https://raw.githubusercontent.com/devsuperior/dscatalog-resources/master/backend/img/26-big.jpg"));
    }

    @Test
    public void findByIdShouldReturnProductDTOWhenProductIdDoesNotExist() throws Exception {
        when(productService.findById(nonExistingId)).thenThrow(ResourceNotFoundException.class);

        ResultActions result =
                mockMvc.perform(get("/products/{id}", nonExistingId)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isNotFound());
    }

    @Test
    public void insertShouldReturnCreateWhenProductDataIsValid() throws Exception {
        when(productService.insert(any())).thenReturn(productDTO);
        String jsonBody = objectMapper.writeValueAsString(productDTO);

        ResultActions result =
                mockMvc.perform(post("/products")
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").exists())
                .andExpect(jsonPath("$.description").exists())
                .andExpect(jsonPath("$.imgUrl").exists());
    }

    @Test
    public void insertShouldReturnUnprocessableEntityWhenNameIsLastThanThreeCharacters() throws Exception {
        productDTO.setName("ab");
        String jsonBody = objectMapper.writeValueAsString(productDTO);

        ResultActions result =
                mockMvc.perform(post("/products")
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isUnprocessableEntity());
    }

    @Test
    public void insertShouldReturnUnprocessableEntityWhenNameIsEmpty() throws Exception {
        productDTO.setName("");
        String jsonBody = objectMapper.writeValueAsString(productDTO);

        ResultActions result =
                mockMvc.perform(post("/products")
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isUnprocessableEntity());
    }

    @Test
    public void insertShouldReturnUnprocessableEntityWhenDescriptionISLastThanFiftyCharacters() throws Exception {
        productDTO.setDescription("Lorem ipsum dolor sit amet");
        String jsonBody = objectMapper.writeValueAsString(productDTO);

        ResultActions result =
                mockMvc.perform(post("/products")
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isUnprocessableEntity());
    }

    @Test
    public void insertShouldReturnUnprocessableEntityWhenDescriptionIsEmpty() throws Exception {
        productDTO.setDescription("");
        String jsonBody = objectMapper.writeValueAsString(productDTO);

        ResultActions result =
                mockMvc.perform(post("/products")
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isUnprocessableEntity());
    }

    @Test
    public void insertShouldReturnUnprocessableEntityWhenPriceIsNegative() throws Exception {
        productDTO.setPrice(-1250.0);
        String jsonBody = objectMapper.writeValueAsString(productDTO);

        ResultActions result =
                mockMvc.perform(post("/products")
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isUnprocessableEntity());
    }

    @Test
    public void insertShouldReturnUnprocessableEntityWhenPriceIsZero() throws Exception {
        productDTO.setPrice(null);
        String jsonBody = objectMapper.writeValueAsString(productDTO);

        ResultActions result =
                mockMvc.perform(post("/products")
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isUnprocessableEntity());
    }

    @Test
    public void insertShouldReturnUnprocessableEntityWhenUrlDoesNotValid() throws Exception {
        productDTO.setImgUrl("http://raw.githubusercontent.com/devsuperior/dscatalog-resources/master/backend/img/26-big.jpg");
        String jsonBody = objectMapper.writeValueAsString(productDTO);

        ResultActions result =
                mockMvc.perform(post("/products")
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isUnprocessableEntity());
    }

    @Test
    public void insertShouldReturnUnprocessableEntityWhenUrlIsEmpty() throws Exception {
        productDTO.setImgUrl("");
        String jsonBody = objectMapper.writeValueAsString(productDTO);

        ResultActions result =
                mockMvc.perform(post("/products")
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isUnprocessableEntity());
    }

    @Test
    public void updateShouldReturnProductDTOWhenDataIsValid() throws Exception {
        when(productService.update(eq(exitingId), any())).thenReturn(productDTO);
        String jsonBody = objectMapper.writeValueAsString(productDTO);

        ResultActions result =
                mockMvc.perform(put("/products/{id}", exitingId)
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(exitingId))
                .andExpect(jsonPath("$.name").value("PC Gamer WW"));
    }

    @Test
    public void updateShouldReturnNotFoundWhenDataDoesNotValid() throws Exception {
        when(productService.update(eq(nonExistingId), any())).thenThrow(ResourceNotFoundException.class);
        String jsonBody = objectMapper.writeValueAsString(productDTO);

        ResultActions result =
                mockMvc.perform(put("/products/{id}", nonExistingId)
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isNotFound());
    }

    @Test
    public void deleteShouldReturnNoContentWhenDataIsValid() throws Exception {
        doNothing().when(productService).delete(exitingId);

        ResultActions result = mockMvc
                .perform(delete("/products/{id}", exitingId)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isNoContent());
    }

    @Test
    public void deleteShouldNotFoundWhenDataDoesNotValid() throws Exception {
        doThrow(ResourceNotFoundException.class).when(productService).delete(nonExistingId);

        ResultActions result = mockMvc
                .perform(delete("/products/{id}", nonExistingId)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isNotFound());
    }

    @Test
    public void deleteShouldBadRequestWhenDataDoesNotValid() throws Exception {
        doThrow(DatabaseException.class).when(productService).delete(dependentId);

        ResultActions result = mockMvc
                .perform(delete("/products/{id}", dependentId)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isBadRequest());
    }
}
