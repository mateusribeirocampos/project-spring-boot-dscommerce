package com.dscommerce.dto;

import com.dscommerce.entities.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.security.core.GrantedAuthority;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class UserDTO {

    private Long id;

    @Size(min = 3, max = 80, message = "Character number must be between 3 and 80")
    @NotBlank(message = "Name cannot be empty")
    private String name;

    private String email;
    private String phone;
    private LocalDate bithDate;

    private List<String> roles = new ArrayList<>();

    public UserDTO(){}

    public UserDTO(Long id, String name, String email, String phone, LocalDate bithDate) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.bithDate = bithDate;
    }

    public UserDTO(User entity) {
        id = entity.getId();
        name = entity.getName();
        email = entity.getEmail();
        phone = entity.getPhone();
        bithDate = entity.getBirthDate();
        for (GrantedAuthority role : entity.getRoles()) {
            roles.add(role.getAuthority());
        }
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
