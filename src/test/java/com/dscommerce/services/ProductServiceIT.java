package com.dscommerce.services;

import com.dscommerce.dto.ProductDTO;
import com.dscommerce.dto.ProductMinDTO;
import com.dscommerce.repositories.ProductRepository;
import com.dscommerce.services.exceptions.ResourceNotFoundException;
import com.dscommerce.tests.ProductFactory;
import com.dscommerce.testsupport.AbstractIntegrationTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
public class ProductServiceIT extends AbstractIntegrationTest {

    private Long existingId, nonExistingId, countTotalProducts;
    private String productName;
    private ProductDTO productDTO;

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    @BeforeEach
    void setUp() throws Exception {
        existingId = 2L;
        nonExistingId = 100L;
        countTotalProducts = 25L;
        productName = "Macbook";
        productDTO = ProductFactory.createProductDTO();
    }

    @Test
    public void findAllShouldReturnProductMinDTOWhenProductNameIsEmpty() {
        PageRequest pageRequest = PageRequest.of(0,10);

        Page<ProductMinDTO> result = productService.findAll("", pageRequest);

        Assertions.assertFalse(result.isEmpty());
        Assertions.assertEquals(0, result.getNumber());
        Assertions.assertEquals(10, result.getSize());
        Assertions.assertEquals(countTotalProducts, result.getTotalElements());
    }

    @Test
    public void findAllShouldReturnProductMinDTOWhenProductNameIsProvided() {
        PageRequest pageRequest = PageRequest.of(0,10);

        Page<ProductMinDTO> result = productService.findAll(productName, pageRequest);

        Assertions.assertFalse(result.isEmpty());
        Assertions.assertEquals(0, result.getNumber());
        Assertions.assertEquals(10, result.getSize());
        Assertions.assertEquals(1L, result.getTotalElements());
    }

    @Test
    public void findByIdShouldReturnProductDTOWhenProductIdExists() {
        ProductDTO result = productService.findById(existingId);
        Assertions.assertEquals(existingId, result.getId());
        Assertions.assertNotNull(result.getName());
    }

    @Test
    public void finByIdShouldReturnResourceNotFoundExceptionWhenIdDoesNotExist() {
        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            productService.findById(nonExistingId);
        });
    }

    @Test
    public void insertShouldReturnProductDTOWhenDataIsValid() {
        ProductDTO result = productService.insert(productDTO);

        Assertions.assertNotNull(result);
        Assertions.assertNotNull(result.getId());
        Assertions.assertEquals(countTotalProducts + 1, productRepository.count());
        Assertions.assertEquals("PC Gamer WW", result.getName());
        Assertions.assertEquals(1350.0, result.getPrice());
    }

    @Test
    public void updateShouldReturnProductDTOWhenDataIsValid() {
        ProductDTO result = productService.update(existingId, productDTO);

        Assertions.assertNotNull(result);
        Assertions.assertNotNull(result.getId());
        Assertions.assertEquals("PC Gamer WW", result.getName());
        Assertions.assertEquals(1350.0, result.getPrice());
    }

    @Test
    public void updateShouldReturnResourceNotFoundWhenDataIsValid() {
        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            productService.update(nonExistingId, productDTO);
        });
    }

    @Test
    public void deleteShouldDeleteWhenIdExist() {
        productService.delete(existingId);
        Assertions.assertEquals(countTotalProducts - 1, productRepository.count());
    }

    @Test
    public void deleteShouldReturnResourceNotFoundWhenIdDoesNotExists() {
        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
           productService.delete(nonExistingId);
        });
    }
}
