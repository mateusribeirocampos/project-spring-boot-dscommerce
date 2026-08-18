package com.dscommerce.controllers;

import com.dscommerce.dto.CategoryDTO;
import com.dscommerce.services.CategoryService;
import com.dscommerce.services.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.test.web.servlet.ResultActions;
import static org.mockito.Mockito.when;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = CategoryController.class, excludeAutoConfiguration = {SecurityAutoConfiguration.class})
public class CategoryControllerTests {

    private CategoryDTO categoryDTO;
    private Long existingId, nonExistingId;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CategoryService categoryService;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() throws Exception {
        existingId = 4L;
        nonExistingId = 99L;
        categoryDTO = new CategoryDTO(4L, "Sport");

    }

    @Test
    public void findAllShouldReturnCategoryDTOWhenCategoryExist() throws Exception {
        when(categoryService.findAll()).thenReturn(List.of(categoryDTO));

        ResultActions result =
                mockMvc.perform(get("/categories")
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(4L))
                .andExpect(jsonPath("$[0].name").value("Sport"));
    }

    @Test
    public void findByIdShouldReturnCategoryDTOWhenCategoryIdExist() throws Exception {
        when(categoryService.findById(existingId)).thenReturn(categoryDTO);

        ResultActions result =
                mockMvc.perform(get("/categories/{id}", existingId)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(existingId))
                .andExpect(jsonPath("$.name").value("Sport"));
    }

    @Test
    public void findByIdShouldReturnResourceNotFoundWhenCategoryIdDoesNotExist() throws Exception {
        when(categoryService.findById(nonExistingId)).thenThrow(ResourceNotFoundException.class);

        ResultActions result =
                mockMvc.perform(get("/categories/{id}", nonExistingId)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isNotFound());
    }
}
