package com.dscommerce.services;

import com.dscommerce.dto.CategoryDTO;
import com.dscommerce.entities.Category;
import com.dscommerce.repositories.CategoryRepository;
import com.dscommerce.services.exceptions.DatabaseException;
import com.dscommerce.services.exceptions.ResourceNotFoundException;
import com.dscommerce.tests.CategoryFactory;
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

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceTests {

    private Category category;
    private CategoryDTO categoryDTO;
    private Long existingId, nonExistingId, dependentId;
    private String categoryName;

    @InjectMocks
    private CategoryService categoryService;

    @Mock
    private CategoryRepository categoryRepository;

    @BeforeEach
    void setUp() throws Exception {
        existingId = 4L;
        nonExistingId = 99L;
        dependentId = 3L;

        categoryName = "Sport";
        category = CategoryFactory.createCategory();
        categoryDTO = CategoryFactory.createCategoryDTO();
    }

    @Test
    public void findByIdShouldReturnCategoryDTOWhenIdIsCorrect() throws Exception {
        Mockito.when(categoryRepository.findById(existingId)).thenReturn(Optional.of(category));
        CategoryDTO result = categoryService.findById(existingId);

        Assertions.assertEquals(existingId, result.getId());
        Assertions.assertEquals(categoryName, result.getName());
    }

    @Test
    public void insertShouldReturnCategoryDTOWhenDataIsCorrect() throws Exception {
        Mockito.when(categoryRepository.save(any())).thenReturn(category);
        CategoryDTO result = categoryService.insert(categoryDTO);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(existingId, result.getId());
    }

    @Test
    public void updateShouldReturnCategoryDTOWhenDataIsCorrect() throws Exception {
        Mockito.when(categoryRepository.getReferenceById(existingId)).thenReturn(category);
        Mockito.when(categoryRepository.save(any())).thenReturn(category);
        CategoryDTO result = categoryService.update(existingId, categoryDTO);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(existingId, result.getId());
        Assertions.assertEquals(categoryName, result.getName());
    }

    @Test
    public void updateShouldReturnNotFoundExceptionWhenDataIsNotCorrect() throws Exception {
        Mockito.when(categoryRepository.getReferenceById(nonExistingId)).thenThrow(EntityNotFoundException.class);

        Assertions.assertThrows(ResourceNotFoundException.class,
                () -> categoryService.update(nonExistingId, categoryDTO));
    }


    @Test
    public void deleteShouldReturnEmptyWhenDataIsCorrect() throws Exception {
        Mockito.when(categoryRepository.existsById(existingId)).thenReturn(true);
        Assertions.assertDoesNotThrow(
                () -> categoryService.delete(existingId));
    }

    @Test
    public void deleteShouldReturnResourceNotFoundWhenIdIsNotCorrect() throws Exception {
        Mockito.when(categoryRepository.existsById(nonExistingId)).thenReturn(false);
        Assertions.assertThrows(ResourceNotFoundException.class,
                () -> categoryService.delete(nonExistingId));
    }

    @Test
    public void deleteShouldReturnEntityViolationWhenDependentId() throws Exception {
        Mockito.when(categoryRepository.existsById(dependentId)).thenReturn(true);
        Mockito.doThrow(DataIntegrityViolationException.class).when(categoryRepository).deleteById(dependentId);
        Assertions.assertThrows(DatabaseException.class,
                () -> categoryService.delete(dependentId));
    }
}
