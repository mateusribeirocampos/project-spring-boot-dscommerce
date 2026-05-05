package com.dscommerce.controllers;

import com.dscommerce.controllers.exceptions.StandardError;
import com.dscommerce.dto.ProductDTO;
import com.dscommerce.dto.ProductMinDTO;
import com.dscommerce.services.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping(value = "/products")
@Tag(name = "Product", description = "Controller for product")
public class ProductController {

    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);

    @Autowired
    private ProductService productService;

    @GetMapping(produces = "application/json")
    @Operation(summary = "Find all products", description = "Controller for visualize all the products",
    responses = {
            @ApiResponse(description = "OK", responseCode = "200"),
            @ApiResponse(description = "Bad Request", responseCode = "400",
                    content = @Content(schema = @Schema(implementation = StandardError.class)))
    })
    public ResponseEntity<Page<ProductMinDTO>> findAll(
            @RequestParam(name = "name", defaultValue = "") String name,
            Pageable pageable) {
        logger.info("GET /products?size=21&page=0&sort=name,desc&name= - finding all products");
        Page<ProductMinDTO> dto = productService.findAll(name, pageable);
        return ResponseEntity.ok(dto);
    }

    @GetMapping(value = "/{id}", produces = "application/json")
    @Operation(summary = "Find product by id", description = "Controller to find product by id",
            responses = {
                    @ApiResponse(description = "OK", responseCode = "200"),
                    @ApiResponse(description = "Bad Request", responseCode = "400",
                            content = @Content(schema = @Schema(implementation = StandardError.class)))
            })
    public ResponseEntity<ProductDTO> findById(@PathVariable Long id) {
        logger.info("GET /products/{} - finding one product by id ", id);
        ProductDTO dto = productService.findById(id);
        return ResponseEntity.ok(dto);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping(produces = "application/json", consumes = "application/json")
    @Operation(summary = "Insert product", description = "Controller to insert product",
            responses = {
                    @ApiResponse(description = "Created", responseCode = "201"),
                    @ApiResponse(description = "Bad Request", responseCode = "400",
                            content = @Content(schema = @Schema(implementation = StandardError.class))),
                    @ApiResponse(description = "Forbidden", responseCode = "403",
                            content = @Content(schema = @Schema(implementation = StandardError.class))),
                    @ApiResponse(description = "Unprocessable Entity", responseCode = "422",
                            content = @Content(schema = @Schema(implementation = StandardError.class)))
            })
    public ResponseEntity<ProductDTO> insert(@Valid @RequestBody ProductDTO dto) {
        logger.info("POST /products - creating a product {}", dto.getName());
        dto = productService.insert(dto);
        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequestUri()
                .path("/{id}")
                .buildAndExpand(dto.getId())
                .toUri();
        return ResponseEntity.created(uri).body(dto);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping(value = "/{id}", produces = "application/json", consumes = "application/json")
    @Operation(summary = "Update product by id", description = "Controller to update product by id",
            responses = {
                    @ApiResponse(description = "OK", responseCode = "200"),
                    @ApiResponse(description = "Bad Request", responseCode = "400",
                            content = @Content(schema = @Schema(implementation = StandardError.class))),
                    @ApiResponse(description = "Forbidden", responseCode = "403",
                            content = @Content(schema = @Schema(implementation = StandardError.class))),
                    @ApiResponse(description = "Unprocessable Entity", responseCode = "422",
                            content = @Content(schema = @Schema(implementation = StandardError.class)))
            })
    public ResponseEntity<ProductDTO> update(@PathVariable Long id,
                                             @Valid @RequestBody ProductDTO dto) {
        logger.info("PUT /products/{} - updating product {} by id", id, dto.getName());
        dto = productService.update(id, dto);
        return ResponseEntity.ok(dto);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping(value = "/{id}")
    @Operation(summary = "Delete product by id", description = "Controller to delete product by id",
            responses = {
                    @ApiResponse(description = "Created", responseCode = "201"),
                    @ApiResponse(description = "Bad Request", responseCode = "400",
                            content = @Content(schema = @Schema(implementation = StandardError.class))),
                    @ApiResponse(description = "Unauthorized", responseCode = "401",
                            content = @Content(schema = @Schema(implementation = StandardError.class))),
                    @ApiResponse(description = "Forbidden", responseCode = "403",
                            content = @Content(schema = @Schema(implementation = StandardError.class))),
                    @ApiResponse(description = "Unprocessable Entity", responseCode = "422",
                            content = @Content(schema = @Schema(implementation = StandardError.class)))
            })
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        logger.info("DELETE /products/{} - deleting product by id", id);
        productService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
