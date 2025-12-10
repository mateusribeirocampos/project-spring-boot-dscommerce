package com.dscommerce.services;

import com.dscommerce.controllers.ProductController;
import com.dscommerce.dto.ProductDTO;
import com.dscommerce.entities.Product;
import com.dscommerce.repositories.ProductRepository;
import com.dscommerce.services.exceptions.DatabaseException;
import com.dscommerce.services.exceptions.ResourceNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);

    @Autowired
    private ProductRepository productRepository;

    @Transactional(readOnly = true)
    public Page<ProductDTO> findAll(String name, Pageable pageable) {
        logger.info("Finding all products");
        Page<Product> productPage = productRepository.searchByName(name, pageable);
        return productPage.map(ProductDTO::new);
    }

    @Transactional(readOnly = true)
    public ProductDTO findById(Long id) {
        logger.info("Finding one product by id: {}", id);
        Optional<Product> result = productRepository.findById(id);
        Product product = result
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found for id: " + id));
        return new ProductDTO(product);
    }

    @Transactional
    public ProductDTO insert(ProductDTO dto) {
        logger.info("Creating a product {}", dto.getName());
        Product entity = new Product();

        dtoToEntity(dto, entity);
        entity = productRepository.save(entity);
        return new ProductDTO(entity);
    }

    private void dtoToEntity(ProductDTO dto, Product entity) {
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setPrice(dto.getPrice());
        entity.setImgUrl(dto.getImgUrl());
    }

    @Transactional
    public ProductDTO update(Long id, ProductDTO dto) {
        logger.info("Updating a product {} by id: {}", dto.getName(), id);
        try {
            Product entity = productRepository.getReferenceById(id);
            System.out.println("SERVICE(after getRef) -> DTO: " + dto.getName() + " and entity: " + entity.getName() );
            dtoToEntity(dto, entity);
            System.out.println("SERVICE(after dtoToEntity) -> DTO: " + dto.getName() + " and entity: " + entity.getName() );
            entity = productRepository.save(entity);
            return new ProductDTO(entity);
        } catch (EntityNotFoundException e) {
            throw new ResourceNotFoundException("Resource not found for id: " + id);
        }
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public void delete(Long id) {
        logger.info("Deleting a product by id: {}", id);
        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException("Resource not found for id: " + id);
        }
        try {
         productRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new DatabaseException("Referential integrity failure");
        }
    }
}
