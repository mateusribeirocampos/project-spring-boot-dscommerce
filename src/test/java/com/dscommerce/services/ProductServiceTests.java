package com.dscommerce.services;

import com.dscommerce.dto.ProductDTO;
import com.dscommerce.dto.ProductMinDTO;
import com.dscommerce.entities.Product;
import com.dscommerce.repositories.CategoryRepository;
import com.dscommerce.repositories.ProductRepository;
import com.dscommerce.services.exceptions.DatabaseException;
import com.dscommerce.services.exceptions.ResourceNotFoundException;
import com.dscommerce.tests.ProductFactory;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTests {

    private Product product;
    private ProductDTO productDTO;
    private PageImpl<Product> pageProduct;
    private String productName;
    private Long exitingId, nonExistingId, dependentId;

    @InjectMocks
    private ProductService productService;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ProductRepository productRepository;

    @BeforeEach
    void setUp() throws Exception {
        exitingId = 1L;
        dependentId = 2L;
        nonExistingId = 100L;
        productName = "PC Gamer WW";

        productDTO = ProductFactory.createProductDTO();
        product = ProductFactory.createProduct();
        pageProduct = new PageImpl<>(List.of(product));

    }

    @Test
    public void findAllShouldReturnPage() {
        Mockito.when(productRepository.searchByName(any(), any(Pageable.class))).thenReturn(pageProduct);
        Pageable pageable = PageRequest.of(0,12);
        Page<ProductMinDTO> result = productService.findAll(productName, pageable);

        Assertions.assertNotNull(result);
        Mockito.verify(productRepository, Mockito.times(1)).searchByName(any(), any(Pageable.class));
    }

    @Test
    public void findByIdShouldReturnProductDTOWhenIdExist() {
        Mockito.when(productRepository.findById(exitingId)).thenReturn(Optional.of(product));
        ProductDTO result = productService.findById(exitingId);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(exitingId, result.getId());
    }

    @Test
    public void findByIdShouldReturnProductDTOWhenIdDoesNotExist() {
        Mockito.when(productRepository.findById(nonExistingId)).thenReturn(Optional.empty());

        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            productService.findById(nonExistingId);
        });
    }

    @Test
    public void insertShouldReturnNotNullWhenDataIsCorrect() {
        Mockito.when(productRepository.save(any())).thenReturn(product);

        ProductDTO result = productService.insert(productDTO);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(exitingId, result.getId());
    }

    @Test
    public void updateShoutReturnNotNullWhenIdExists() {
        Mockito.when(productRepository.getReferenceById(exitingId)).thenReturn(product);
        Mockito.when(productRepository.save(any())).thenReturn(product);
        Mockito.when(categoryRepository.getReferenceById(any())).thenReturn(ProductFactory.createCategory());

        ProductDTO result = productService.update(exitingId, productDTO);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(exitingId, result.getId());
        Assertions.assertEquals(productName, result.getName());
    }

    @Test
    public void updateShouldReturnExceptionWhenIdDoesNotExists() {
        Mockito.when(productRepository.getReferenceById(nonExistingId)).thenThrow(EntityNotFoundException.class);

        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            productService.update(nonExistingId, productDTO);
        });
    }

    @Test
    public void deleteShouldReturnNothingWhenIdExists() {
        Mockito.when(productRepository.existsById(exitingId)).thenReturn(true);

        Assertions.assertDoesNotThrow(() -> {
            productService.delete(exitingId);
        });
    }

    @Test
    public void deleteShouldReturnDatabaseExceptionWhenDependentId() {
        Mockito.when(productRepository.existsById(dependentId)).thenReturn(true);
        Mockito.doThrow(DataIntegrityViolationException.class).when(productRepository).deleteById(dependentId);

        Assertions.assertThrows(DatabaseException.class, () -> {
            productService.delete(dependentId);
        });
    }

    @Test
    public void deleteShouldReturnResourceNotFoundExceptionWhenIdDoesNotExists() {
        Mockito.when(productRepository.existsById(nonExistingId)).thenReturn(false);

        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            productService.delete(nonExistingId);
        });
    }
}