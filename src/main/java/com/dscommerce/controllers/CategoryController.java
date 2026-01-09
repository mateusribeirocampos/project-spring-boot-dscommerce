package com.dscommerce.controllers;

import com.dscommerce.dto.CategoryDTO;
import com.dscommerce.services.CategoryService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping(value = "/categories")
public class CategoryController {

    private static final Logger logger = LoggerFactory.getLogger(CategoryController.class);

    @Autowired
    private CategoryService categoryService;

    @GetMapping
    public ResponseEntity<List<CategoryDTO>> findAll() {
        logger.info("GET /categories - finding all categories");
        List<CategoryDTO> dto = categoryService.findAll();
        return ResponseEntity.ok(dto);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<CategoryDTO> findById(@PathVariable Long id) {
        logger.info("GET /categories/{} - finding one product by id ", id);
        CategoryDTO dto = categoryService.findById(id);
        return ResponseEntity.ok(dto);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping
    public ResponseEntity<CategoryDTO> insert(@Valid @RequestBody CategoryDTO dto) {
        logger.info("POST /categories - creating a category {}", dto.getName());
        dto = categoryService.insert(dto);
        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequestUri()
                .path("/{id}")
                .buildAndExpand(dto.getId())
                .toUri();
        return ResponseEntity.created(uri).body(dto);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping(value = "/{id}")
    public ResponseEntity<CategoryDTO> update(@Valid @PathVariable Long id,
                                             @RequestBody CategoryDTO dto) {
        logger.info("PUT /categorys/{} - updating category {} by id", id, dto.getName());
        dto = categoryService.update(id, dto);
        return ResponseEntity.ok(dto);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        logger.info("DELETE /categorys/{} - deleting category by id", id);
        categoryService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
