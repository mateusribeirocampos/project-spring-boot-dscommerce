package com.dscommerce.controllers;

import com.dscommerce.controllers.exceptions.StandardError;
import com.dscommerce.dto.UserDTO;
import com.dscommerce.dto.UserInsertDTO;
import com.dscommerce.dto.UserUpdateDTO;
import com.dscommerce.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping(value = "/users")
@Tag(name = "Users", description = "Controller for users")
public class UserController {

    @Autowired
    private UserService userService;

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_CLIENT')")
    @GetMapping(value = "/me", produces = "application/json")
    @Operation(summary ="Profile", description = "Controller for visualize user profile",
    responses = {
            @ApiResponse(description = "OK", responseCode = "200"),
            @ApiResponse(description = "Unauthorized", responseCode = "401",
                    content = @Content(schema = @Schema(implementation = StandardError.class))),
            @ApiResponse(description = "Forbidden", responseCode = "403",
                    content = @Content(schema = @Schema(implementation = StandardError.class)))
    })
    public ResponseEntity<UserDTO> getMe() {
        UserDTO dto = userService.getMe();
        return ResponseEntity.ok(dto);
    }

    @PostMapping(value = "/register", produces = "application/json", consumes = "application/json")
    @Operation(summary = "Register", description = "Controller for register users",
    responses = {
            @ApiResponse(description = "Created", responseCode = "201"),
            @ApiResponse(description = "Unprocessable Entity", responseCode = "422",
                    content = @Content(schema = @Schema(implementation = StandardError.class)))
    })
    public ResponseEntity<UserDTO> register(@Valid @RequestBody UserInsertDTO dto) {
        UserDTO newDto = userService.register(dto);
        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(newDto.getId())
                .toUri();
        return ResponseEntity.created(uri).body(newDto);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_CLIENT')")
    @PutMapping(value = "/me", produces = "application/json", consumes = "application/json")
    @Operation(summary = "Update profile", description = "Controller for update user profile",
    responses = {
            @ApiResponse(description = "OK", responseCode = "200"),
            @ApiResponse(description = "Unauthorized", responseCode = "401",
                    content = @Content(schema = @Schema(implementation = StandardError.class))),
            @ApiResponse(description = "Forbidden", responseCode = "403",
                    content = @Content(schema = @Schema(implementation = StandardError.class))),
            @ApiResponse(description = "Not Found", responseCode = "404",
                    content = @Content(schema = @Schema(implementation = StandardError.class))),
            @ApiResponse(description = "Unprocessable Entity", responseCode = "422",
                    content = @Content(schema = @Schema(implementation = StandardError.class)))
    })
    public ResponseEntity<UserDTO> updateMe(@Valid @RequestBody UserUpdateDTO dto) {
        UserDTO userUpdateDTO = userService.updateMe(dto);
        return ResponseEntity.ok(userUpdateDTO);
    }

}
