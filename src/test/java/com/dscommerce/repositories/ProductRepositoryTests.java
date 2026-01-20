package com.dscommerce.repositories;

import com.dscommerce.entities.Product;
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

    @Autowired
    private ProductRepository productRepository;

    @BeforeEach
    public void setup() {
        existingId = 1L;
        nonExistingId = 2L;
    }


    // DELETE
    @Test
    public void deleteShouldDeleteObjectWhenIdExists() {
        productRepository.deleteById(existingId);
        Optional<Product> product = productRepository.findById(existingId);
        Assertions.assertFalse(product.isPresent());
    }

    @Test
    public void deleteShouldDeleteObjectWhenIdDoesNotExist() {

        Assertions.assertDoesNotThrow(() -> productRepository.deleteById(nonExistingId));
    }
}
