package com.dscommerce.controllers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.dscommerce.dto.UserDTO;
import com.dscommerce.dto.UserInsertDTO;
import com.dscommerce.dto.UserUpdateDTO;
import com.dscommerce.tests.TokenUtil;
import com.dscommerce.tests.UserFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class UserControllerIT {

    private String username, password, bearerToken, bearerTokenInvalid;
    private Long existingId;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TokenUtil tokenUtil;

    @BeforeEach
    void setUp() throws Exception {
        existingId = 1L;
        username = "maria@gmail.com";
        password = "123456";

        bearerToken = tokenUtil.obtainAccessToken(mockMvc, username, password);
        bearerTokenInvalid = bearerToken + "zyx";

    }

    @Test
    public void getMeShouldReturnUserDTOWhenUserIsAuthenticated() throws Exception {
        UserDTO userDto = UserFactory.createUserDTO();
        String jsonBody = objectMapper.writeValueAsString(userDto);

        ResultActions result =
                mockMvc.perform(get("/users/me")
                        .header("Authorization", "Bearer " + bearerToken)
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.id").value(existingId));
        result.andExpect(jsonPath("$.name").value("Maria Brown"));
        result.andExpect(jsonPath("$.email").value("maria@gmail.com"));
        result.andExpect(jsonPath("$.phone").value("988888888"));
        result.andExpect(jsonPath("$.birthDate").value("2001-07-25"));
    }

    @Test
    public void getMeShouldReturnUnauthorizedWhenUserDoesNotAuthenticated() throws Exception {
        ResultActions result =
                mockMvc.perform(get("/users/me")
                        .header("Authorization", "Bearer " + bearerTokenInvalid)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isUnauthorized());

    }

    @Test
    public void registerShouldReturnUserDTOWhenUserDataIsValid() throws Exception {
        UserInsertDTO userInsertDTO = UserFactory.createUserInsertDTO();
        userInsertDTO.setName("John Winter");
        userInsertDTO.setEmail("jonh@gmail.com");
        userInsertDTO.setPassword("12345678");
        userInsertDTO.setPhone("911111111");
        userInsertDTO.setBirthDate(LocalDate.parse("1983-09-09"));

        String jsonBody = objectMapper.writeValueAsString(userInsertDTO);

        ResultActions result =
                mockMvc.perform(post("/users/register")
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isCreated());
        result.andExpect(jsonPath("$.id").exists());
        result.andExpect(jsonPath("$.name").value("John Winter"));
        result.andExpect(jsonPath("$.email").value("jonh@gmail.com"));
    }

    @Test
    public void registerShouldReturnUnprocessableEntityWhenUserNameIsBlank() throws Exception {
        UserInsertDTO userInsertDTO = UserFactory.createUserInsertDTO();
        userInsertDTO.setName(" ");
        userInsertDTO.setEmail("jonh@gmail.com");
        userInsertDTO.setPassword("12345678");
        userInsertDTO.setPhone("911111111");
        userInsertDTO.setBirthDate(LocalDate.parse("1983-09-09"));

        String jsonBody = objectMapper.writeValueAsString(userInsertDTO);

        ResultActions result =
                mockMvc.perform(post("/users/register")
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isUnprocessableEntity());
    }

    @Test
    public void registerShouldReturnUnprocessableEntityWhenUserEmailIsBlank() throws Exception {
        UserInsertDTO userInsertDTO = UserFactory.createUserInsertDTO();
        userInsertDTO.setName("John Winter");
        userInsertDTO.setEmail(" ");
        userInsertDTO.setPassword("12345678");
        userInsertDTO.setPhone("911111111");
        userInsertDTO.setBirthDate(LocalDate.parse("1983-09-09"));

        String jsonBody = objectMapper.writeValueAsString(userInsertDTO);

        ResultActions result =
                mockMvc.perform(post("/users/register")
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isUnprocessableEntity());
    }

    @Test
    public void registerShouldReturnUnprocessableEntityWhenUserPasswordIsBlank() throws Exception {
        UserInsertDTO userInsertDTO = UserFactory.createUserInsertDTO();
        userInsertDTO.setName("John Winter");
        userInsertDTO.setEmail("jonh@gmail.com");
        userInsertDTO.setPassword(" ");
        userInsertDTO.setPhone("911111111");
        userInsertDTO.setBirthDate(LocalDate.parse("1983-09-09"));

        String jsonBody = objectMapper.writeValueAsString(userInsertDTO);

        ResultActions result =
                mockMvc.perform(post("/users/register")
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isUnprocessableEntity());
    }

    @Test
    public void updateMeShouldReturnUserUpdateDTOWhenUserIsValid() throws Exception {
        UserUpdateDTO userUpdateDTO = UserFactory.updateUserDTO();
        String jsonBody = objectMapper.writeValueAsString(userUpdateDTO);

        ResultActions result =
                mockMvc.perform(put("/users/me")
                        .header("Authorization", "Bearer " + bearerToken)
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.name").value("Maria White"));
        result.andExpect(jsonPath("$.email").value("mariawhite@gmail.com"));
        result.andExpect(jsonPath("$.phone").value("977777777"));
        result.andExpect(jsonPath("$.birthDate").value("1983-07-25"));
    }

    @Test
    public void updateMeShouldReturnUnauthorizedWhenUserIsNotAuthenticated() throws Exception {
        UserUpdateDTO userUpdateDTO = UserFactory.updateUserDTO();
        String jsonBody = objectMapper.writeValueAsString(userUpdateDTO);

        ResultActions result =
                mockMvc.perform(put("/users/me")
                        .header("Authorization", "Bearer " + bearerTokenInvalid)
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isUnauthorized());
    }
}
