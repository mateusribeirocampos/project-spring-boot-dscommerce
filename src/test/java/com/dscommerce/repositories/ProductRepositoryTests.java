package com.dscommerce.repositories;

import com.dscommerce.entities.Product;
import com.dscommerce.tests.ProductFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;


@DataJpaTest
public class ProductRepositoryTests {

    private long existingId;
    private long nonExistingId;
    private long countTotalProduct;

    @Autowired
    private ProductRepository productRepository;

    @BeforeEach
    public void setup() {
        existingId = 1L;
        nonExistingId = 1000L;
        countTotalProduct = 25L;
    }

    // Delete
    @Test
    public void deleteShouldDeleteObjectWhenIdExists() {
        productRepository.deleteById(existingId);
        Optional<Product> product = productRepository.findById(existingId);
        Assertions.assertFalse(product.isPresent());
    }

    @Test
    public void deleteShouldDeleteObjectWhenIdDoesNotExists() {

        Assertions.assertDoesNotThrow(() -> productRepository.deleteById(nonExistingId));
    }

    // findById
    @Test
    public void findByIdShouldReturnProductWhenIdExists() {
        Optional<Product> product = productRepository.findById(existingId);
        Assertions.assertTrue(product.isPresent());
    }

    @Test
    public void findByIShouldReturnEmptyWhenIdDoesNotExists() {
        Optional<Product> product = productRepository.findById(nonExistingId);
        Assertions.assertTrue(product.isEmpty());
    }

    // save
    @Test
    public void saveShouldPersistWithAutoincrementWhenIdIsNull() {
        Product product = ProductFactory.createProduct();
        product.setId(null);
        product =  productRepository.save(product);

        Assertions.assertNotNull(product.getId());
        Assertions.assertEquals(countTotalProduct + 1, product.getId());
    }
}
