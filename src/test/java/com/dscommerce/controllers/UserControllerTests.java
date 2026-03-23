package com.dscommerce.controllers;

import com.dscommerce.dto.UserDTO;
import com.dscommerce.dto.UserInsertDTO;
import com.dscommerce.dto.UserUpdateDTO;
import com.dscommerce.repositories.UserRepository;
import com.dscommerce.services.UserService;
import com.dscommerce.tests.UserFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = UserController.class, excludeAutoConfiguration = {SecurityAutoConfiguration.class})
public class UserControllerTests {

    private UserDTO userDTO;
    private UserInsertDTO userInsertDTO;
    private UserUpdateDTO userUpdateDTO;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() throws Exception {
        userDTO = UserFactory.createUserDTO();
        userInsertDTO = UserFactory.createUserInsertDTO();
        userUpdateDTO = UserFactory.updateUserDTO();

        var jwt = Jwt.withTokenValue("token")
                .header("alg", "RS256")
                .claim("username", "maria@gmail.com")
                .build();
        var authentication = new JwtAuthenticationToken(jwt);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @AfterEach
    void afterEach() throws Exception {
        SecurityContextHolder.clearContext();
    }

    @Test
    public void getMeShouldReturnUserDTOWhenUserExist() throws Exception {
        when(userService.getMe()).thenReturn(userDTO);

        ResultActions result =
                mockMvc.perform(get("/users/me")
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Maria Brown"))
                .andExpect(jsonPath("$.email").value("maria@gmail.com"));

    }

    @Test
    public void registerShouldReturnNewUserDTOWhenDataUserIsValid() throws Exception {
        when(userService.register(any())).thenReturn(userDTO);
        String jsonBody = objectMapper.writeValueAsString(userInsertDTO);

        ResultActions result =
                mockMvc.perform(post("/users/register")
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isCreated());
        result.andExpect(jsonPath("$.id").exists());
        result.andExpect(jsonPath("$.name").value("Maria Brown"));
        result.andExpect(jsonPath("$.email").value("maria@gmail.com"));
        result.andExpect(jsonPath("$.phone").value("988888888"));
        result.andExpect(jsonPath("$.birthDate").value("1983-07-25"));
    }

    @Test
    public void updateMeShouldReturnUserUpdateDTOWhenUserIsValid() throws Exception {
        when(userRepository.findByEmail(any())).thenReturn(UserFactory.createUser());
        when(userService.updateMe(any())).thenReturn(userDTO);
        String jsonBody = objectMapper.writeValueAsString(userUpdateDTO);

        ResultActions result = mockMvc
                .perform(put("/users/me")
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.id").exists());
        result.andExpect(jsonPath("$.name").value("Maria Brown"));
        result.andExpect(jsonPath("$.email").value("maria@gmail.com"));
        result.andExpect(jsonPath("$.phone").value("988888888"));
        result.andExpect(jsonPath("$.birthDate").value("1983-07-25"));

    }
}
