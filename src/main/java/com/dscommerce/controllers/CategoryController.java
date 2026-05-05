package com.dscommerce.controllers;

import com.dscommerce.controllers.exceptions.StandardError;
import com.dscommerce.dto.CategoryDTO;
import com.dscommerce.services.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Category", description = "Controller for categories")
public class CategoryController {

    private static final Logger logger = LoggerFactory.getLogger(CategoryController.class);

    @Autowired
    private CategoryService categoryService;

    @GetMapping(produces = "application/json")
    @Operation(summary = "Find all categories", description = "Controller to find all the categories",
            responses = {
                    @ApiResponse(description = "OK", responseCode = "200"),
                    @ApiResponse(description = "Bad Request", responseCode = "400",
                            content = @Content(schema = @Schema(implementation = StandardError.class)))
            })
    public ResponseEntity<List<CategoryDTO>> findAll() {
        logger.info("GET /categories - finding all categories");
        List<CategoryDTO> dto = categoryService.findAll();
        return ResponseEntity.ok(dto);
    }

    @GetMapping(value = "/{id}", produces = "application/json")
    @Operation(summary = "Find category by id", description = "Controller to find category by id",
            responses = {
                    @ApiResponse(description = "OK", responseCode = "200"),
                    @ApiResponse(description = "Bad Request", responseCode = "400",
                            content = @Content(schema = @Schema(implementation = StandardError.class))),
            })
    public ResponseEntity<CategoryDTO> findById(@PathVariable Long id) {
        logger.info("GET /categories/{} - finding one product by id ", id);
        CategoryDTO dto = categoryService.findById(id);
        return ResponseEntity.ok(dto);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping(produces = "application/json", consumes = "application/json")
    @Operation(summary = "Insert category", description = "Controller to insert category",
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
    @PutMapping(value = "/{id}", produces = "application/json", consumes = "application/json")
    @Operation(summary = "Update category by id", description = "Controller to update categories",
            responses = {
                    @ApiResponse(description = "OK", responseCode = "200"),
                    @ApiResponse(description = "Bad Request", responseCode = "400",
                            content = @Content(schema = @Schema(implementation = StandardError.class))),
                    @ApiResponse(description = "Unauthorized", responseCode = "401",
                            content = @Content(schema = @Schema(implementation = StandardError.class))),
                    @ApiResponse(description = "Forbidden", responseCode = "403",
                            content = @Content(schema = @Schema(implementation = StandardError.class))),
                    @ApiResponse(description = "Not Found", responseCode = "404",
                            content = @Content(schema = @Schema(implementation = StandardError.class))),
                    @ApiResponse(description = "Unprocessable Entity", responseCode = "422",
                            content = @Content(schema = @Schema(implementation = StandardError.class)))
            })
    public ResponseEntity<CategoryDTO> update(@Valid @PathVariable Long id,
                                             @RequestBody CategoryDTO dto) {
        logger.info("PUT /categorys/{} - updating category {} by id", id, dto.getName());
        dto = categoryService.update(id, dto);
        return ResponseEntity.ok(dto);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping(value = "/{id}")
    @Operation(summary = "Delete category by id", description = "Controller to delete category by id",
            responses = {
                    @ApiResponse(description = "No Content", responseCode = "204"),
                    @ApiResponse(description = "Bad Request", responseCode = "400",
                            content = @Content(schema = @Schema(implementation = StandardError.class))),
                    @ApiResponse(description = "Unauthorized", responseCode = "401",
                            content = @Content(schema = @Schema(implementation = StandardError.class))),
                    @ApiResponse(description = "Forbidden", responseCode = "403",
                            content = @Content(schema = @Schema(implementation = StandardError.class))),
                    @ApiResponse(description = "Not Found", responseCode = "404",
                            content = @Content(schema = @Schema(implementation = StandardError.class)))
            })
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        logger.info("DELETE /categorys/{} - deleting category by id", id);
        categoryService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
