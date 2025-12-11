package com.dscommerce.services;

import com.dscommerce.dto.CategoryDTO;
import com.dscommerce.entities.Category;
import com.dscommerce.repositories.CategoryRepository;
import com.dscommerce.services.exceptions.DatabaseException;
import com.dscommerce.services.exceptions.ResourceNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CategoryService {
    
    private static final Logger logger = LoggerFactory.getLogger(CategoryService.class);

    @Autowired
    private CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public List<CategoryDTO> findAll() {
        logger.info("Finding all category");
        List<Category> catList = categoryRepository.findAll();
        return catList.stream().map(CategoryDTO::new).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CategoryDTO findById(Long id) {
        logger.info("Finding one category by id: {}", id);
        Optional<Category> result = categoryRepository.findById(id);
        Category Category = result
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found for id: " + id));
        return new CategoryDTO(Category);
    }

    @Transactional
    public CategoryDTO insert(CategoryDTO dto) {
        logger.info("Creating a category {}", dto.getName());
        Category entity = new Category();

        dtoToEntity(dto, entity);
        entity = categoryRepository.save(entity);
        return new CategoryDTO(entity);
    }

    private void dtoToEntity(CategoryDTO dto, Category entity) {
        entity.setName(dto.getName());
    }

    @Transactional
    public CategoryDTO update(Long id, CategoryDTO dto) {
        logger.info("Updating a Category {} by id: {}", dto.getName(), id);
        try {
            Category entity = categoryRepository.getReferenceById(id);
            dtoToEntity(dto, entity);
            entity = categoryRepository.save(entity);
            return new CategoryDTO(entity);
        } catch (EntityNotFoundException e) {
            throw new ResourceNotFoundException("Resource not found for id: " + id);
        }
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public void delete(Long id) {
        logger.info("Deleting a Category by id: {}", id);
        if (!categoryRepository.existsById(id)) {
            throw new ResourceNotFoundException("Resource not found for id: " + id);
        }
        try {
            categoryRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new DatabaseException("Referential integrity failure");
        }
    }
}
