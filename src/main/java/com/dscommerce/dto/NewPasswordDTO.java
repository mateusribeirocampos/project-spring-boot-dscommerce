package com.dscommerce.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class NewPasswordDTO {

    @NotBlank(message = "The field must not be empty")
    private String token;

    @NotBlank(message = "The field must not be empty")
    @Size(min = 8, message = "The password must contain at least 8 characters")
    private String password;

    public NewPasswordDTO() {
    }

    public NewPasswordDTO(String token, String password) {
        this.token = token;
        this.password = password;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
