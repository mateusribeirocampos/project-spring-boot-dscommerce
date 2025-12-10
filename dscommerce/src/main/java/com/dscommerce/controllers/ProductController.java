package com.dscommerce.controllers;

import com.dscommerce.dto.ProductDTO;
import com.dscommerce.entities.Product;
import com.dscommerce.services.ProductService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping(value = "/products")
public class ProductController {

    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);

    @Autowired
    private ProductService productService;

    @GetMapping
    public ResponseEntity<Page<ProductDTO>> findAll(
            @RequestParam(name = "name", defaultValue = "") String name,
            Pageable pageable) {
        logger.info("GET /products?size=21&page=0&sort=name,desc&name=pc%20gamer - finding all products");
        Page<ProductDTO> dto = productService.findAll(name, pageable);
        return ResponseEntity.ok(dto);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<ProductDTO> findById(@PathVariable Long id) {
        logger.info("GET /products/{} - finding one product by id ", id);
        ProductDTO dto = productService.findById(id);
        return ResponseEntity.ok(dto);
    }

    @PostMapping
    public ResponseEntity<ProductDTO> insert(@Valid @RequestBody ProductDTO dto) {
        logger.info("POST /products - creating a product {}", dto.getName());
        dto = productService.insert(dto);
        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequestUri()
                .path("{id}")
                .buildAndExpand(dto.getId())
                .toUri();
        return ResponseEntity.created(uri).body(dto);
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<ProductDTO> update(@PathVariable Long id,
                                             @Valid @RequestBody ProductDTO dto) {
        logger.info("PUT /products/{} - updating product {} by id", id, dto.getName());
        dto = productService.update(id, dto);
        System.out.println("Controller layer: dto: " + dto.getName());
        return ResponseEntity.ok(dto);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        logger.info("DELETE /products/{} - deleting product by id", id);
        productService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
