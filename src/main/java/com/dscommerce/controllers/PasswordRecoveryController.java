package com.dscommerce.controllers;

import com.dscommerce.controllers.exceptions.StandardError;
import com.dscommerce.dto.ForgotPasswordDTO;
import com.dscommerce.dto.NewPasswordDTO;
import com.dscommerce.services.PasswordRecoveryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@Tag(name = "Password Recovery", description = "Controller for password recovery")
public class PasswordRecoveryController {

    @Autowired
    private PasswordRecoveryService passwordRecoveryService;

    @PostMapping(value = "/forgot-password", consumes = "application/json")
    @Operation(summary = "Forgot Password", description = "Controller for password recovery",
            responses = {
                    @ApiResponse(description = "No Content", responseCode = "204"),
                    @ApiResponse(description = "Not Found", responseCode = "404",
                            content = @Content(schema = @Schema(implementation = StandardError.class))),
            })
    public ResponseEntity<Void> forgotPassword(@Valid @RequestBody ForgotPasswordDTO dto) {
        passwordRecoveryService.createRecoveryToken(dto);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/reset-password")
    @Operation(summary = "Reset Password", description = "Controller for reset password",
            responses = {
                    @ApiResponse(description = "No Content", responseCode = "204")
            })
    public ResponseEntity<Void> resetPassword(@Valid @RequestBody NewPasswordDTO dto) {
        passwordRecoveryService.saveNewPassword(dto);
        return ResponseEntity.noContent().build();
    }
}