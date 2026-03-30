package com.dscommerce.services;

import com.dscommerce.dto.UserDTO;
import com.dscommerce.dto.UserInsertDTO;
import com.dscommerce.dto.UserUpdateDTO;
import com.dscommerce.repositories.UserRepository;
import com.dscommerce.tests.UserFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
public class UserServiceIT {

    private String existingEmail, nonExistingEmail, usernameAdmin;
    private Long countTotalUsers;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() throws Exception {
        existingEmail = "maria@gmail.com";
        nonExistingEmail = "email@gmail.com";
        countTotalUsers = 2L;

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
    public void registerShouldReturnUserDTOWhenDataIsValid() {
        UserInsertDTO dto = UserFactory.createUserInsertDTO();
        dto.setEmail("newuser@gmail.com");
        UserDTO result = userService.register(dto);

        Assertions.assertNotNull(result.getId());
        Assertions.assertEquals("newuser@gmail.com", result.getEmail());
        Assertions.assertEquals(countTotalUsers + 1, userRepository.count());
    }

    @Test
    public void registerShouldReturnDataIntegrityViolationWhenEmailAlreadyExist() {
        UserInsertDTO dto = UserFactory.createUserInsertDTO();
        Assertions.assertThrows(DataIntegrityViolationException.class, () -> {
            userService.register(dto);
        });
    }

    @Test
    public void loadUserByUsernameShouldReturnUserDetailsWhenUserExist() {
        UserDetails result = userService.loadUserByUsername(existingEmail);
        Assertions.assertNotNull(result);
        Assertions.assertEquals(result.getUsername(), existingEmail);
    }

    @Test
    public void loadUserByUsernameShouldReturnUsernameNotFoundExceptionWhenUserExist() {
        Assertions.assertThrows(UsernameNotFoundException.class, () -> {
            userService.loadUserByUsername(nonExistingEmail);
        });
    }

    @Test
    public void updateMeShouldReturnUserUpdateDTOWhenDataIsValid() {
        UserUpdateDTO userUpdateDTO = UserFactory.updateUserDTO();
        UserDTO result = userService.updateMe(userUpdateDTO);

        Assertions.assertNotNull(result);
        Assertions.assertEquals("Maria White", result.getName());
        Assertions.assertEquals("mariawhite@gmail.com", result.getEmail());
    }

    @Test
    public void updateMeShouldReturnUsernameNotFoundExceptionWhenDataDoesNotValid() {
        UserUpdateDTO userUpdateDTO = UserFactory.updateUserDTO();
        SecurityContextHolder.clearContext();
        Assertions.assertThrows(UsernameNotFoundException.class, () -> {
            userService.updateMe(userUpdateDTO);
        });
    }
}
