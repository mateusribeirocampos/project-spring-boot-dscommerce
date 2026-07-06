package com.dscommerce.controllers;

import com.dscommerce.controllers.exceptions.StandardError;
import com.dscommerce.dto.OrderDTO;
import com.dscommerce.dto.OrderSummaryDTO;
import com.dscommerce.services.OrderService;
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
import java.util.List;

@RestController
@RequestMapping(value = "/orders")
@Tag(name = "Order", description = "Controller for order")
public class OrderController {

    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

    @Autowired
    private OrderService orderService;

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping(produces = "application/json")
    @Operation(summary = "Find all orders", description = "Controller to find all the orders",
            responses = {
                    @ApiResponse(description = "OK", responseCode = "200"),
                    @ApiResponse(description = "Bad Request", responseCode = "400",
                            content = @Content(schema = @Schema(implementation = StandardError.class))),
                    @ApiResponse(description = "Unauthorized", responseCode = "401",
                            content = @Content(schema = @Schema(implementation = StandardError.class))),
                    @ApiResponse(description = "Forbidden", responseCode = "403",
                            content = @Content(schema = @Schema(implementation = StandardError.class))),
            })
    public ResponseEntity<Page<OrderSummaryDTO>> findAll(
            @RequestParam(name = "clientName", defaultValue = "") String clientName,
            Pageable pageable) {
        logger.info("GET /orders?size=21&page=0&sort=moment,asc&clientName= - finding all orders");
        Page<OrderSummaryDTO> dto = orderService.findAll(clientName, pageable);
        return ResponseEntity.ok(dto);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_CLIENT')")
    @GetMapping(value = "/{id}", produces = "application/json")
    @Operation(summary = "Find order by id", description = "Controller to find order by id",
    responses = {
            @ApiResponse(description = "OK", responseCode = "200"),
            @ApiResponse(description = "Bad Request", responseCode = "400",
                    content = @Content(schema = @Schema(implementation = StandardError.class))),
            @ApiResponse(description = "Unauthorized", responseCode = "401",
                    content = @Content(schema = @Schema(implementation = StandardError.class))),
            @ApiResponse(description = "Forbidden", responseCode = "403",
                    content = @Content(schema = @Schema(implementation = StandardError.class))),
            @ApiResponse(description = "Not Found", responseCode = "404",
                    content = @Content(schema = @Schema(implementation = StandardError.class)))
    })
    public ResponseEntity<OrderDTO> findById(@PathVariable Long id) {
        logger.info("GET /orders/{} - finding one order by id ", id);
        OrderDTO dto = orderService.findById(id);
        return ResponseEntity.ok(dto);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_CLIENT')")
    @PostMapping(produces = "application/json", consumes = "application/json")
    @Operation(summary = "Insert order", description = "Controller to insert order",
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
    public ResponseEntity<OrderDTO> insert(@Valid @RequestBody OrderDTO dto) {
        logger.info("POST /orders - creating a order {}", dto.getClient());
        dto = orderService.insert(dto);
        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequestUri()
                .path("/{id}")
                .buildAndExpand(dto.getId())
                .toUri();
        return ResponseEntity.created(uri).body(dto);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping(value = "/{id}", produces = "application/json", consumes = "application/json")
    @Operation(summary = "Update order by id", description = "Controller to update order",
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
    public ResponseEntity<OrderDTO> update(@PathVariable Long id,
                                             @Valid @RequestBody OrderDTO dto) {
        logger.info("PUT /orders/{} - updating order {} by id", id, dto.getClient());
        dto = orderService.update(id, dto);
        return ResponseEntity.ok(dto);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping(value = "/{id}")
    @Operation(summary = "Delete order by id", description = "Controller to delete order by id",
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
        logger.info("DELETE /orders/{} - deleting order by id", id);
        orderService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
