package com.dscommerce.dto;

import com.dscommerce.entities.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public class UserDTO {

    private Long id;

    @Size(min = 3, max = 80, message = "Character number must be between 3 and 80")
    @NotBlank(message = "Name cannot be empty")
    private String name;

    public UserDTO(){}

    public UserDTO(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public UserDTO(User entity) {
        id = entity.getId();
        name = entity.getName();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
