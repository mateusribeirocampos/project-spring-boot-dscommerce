package com.dscommerce.services;

import com.dscommerce.dto.UserDTO;
import com.dscommerce.dto.UserInsertDTO;
import com.dscommerce.dto.UserUpdateDTO;
import com.dscommerce.entities.Role;
import com.dscommerce.entities.User;
import com.dscommerce.repositories.RoleRepository;
import com.dscommerce.repositories.UserRepository;
import com.dscommerce.tests.UserFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
public class UserServiceTests {

    private String existingEmail, nonExistingEmail, passwordMock;
    private User user;
    private UserInsertDTO userInsertDTO;
    private UserUpdateDTO userUpdateDTO;

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() throws Exception {
        existingEmail = "maria@gmail.com";
        nonExistingEmail = "myemail@gmail.com";
        passwordMock = "12345678";
        user = UserFactory.createUser();
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
    public void getMeShouldReturnNotNullWhenEmailExist() {
        Mockito.when(userRepository.findByEmail(existingEmail)).thenReturn(user);
        UserDTO result = userService.getMe();

        Assertions.assertNotNull(result);
        Assertions.assertEquals(existingEmail, result.getEmail());
    }

    @Test
    public void securityContextHolderShouldReturnExceptionWhenSecurityContextHolderIsEmpty() {
        SecurityContextHolder.clearContext();
        Assertions.assertThrows(UsernameNotFoundException.class, () -> {
           userService.getMe();
        });
    }

    @Test
    public void loadUserByUsernameShouldReturnNotNullWhenEmailExist() {
        Mockito.when(userRepository.searchUserAndRolesByEmail(existingEmail))
                .thenReturn(UserFactory.createUserDetailsProjectionList());

        UserDetails result = userService.loadUserByUsername(existingEmail);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(existingEmail, result.getUsername());
    }

    @Test
    public void loadUserByUsernameShouldReturnExceptionWhenEmailIsEmpty() {
        Mockito.when(userRepository.searchUserAndRolesByEmail(nonExistingEmail))
                .thenReturn(List.of());

        Assertions.assertThrows(UsernameNotFoundException.class, () -> {
            userService.loadUserByUsername(nonExistingEmail);
        });
    }

    @Test
    public void registerShouldReturnNotNullWhenRoleAndPasswordWereProvide() {
        Mockito.when(userRepository.save(any())).thenReturn(user); //Cria User entity internamente (campos do DTO — podem ser null, não importa pro teste)
        Mockito.when(roleRepository.findByAuthority(any())).thenReturn(new Role()); // roleRepository.findByAuthority() → retorna new Role()
        Mockito.when(passwordEncoder.encode(any())).thenReturn(passwordMock); // passwordEncoder.encode() → retorna passwordMock

        UserDTO result = userService.register(userInsertDTO);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(existingEmail, result.getEmail());
    }

    @Test
    public void updateMeShouldReturnUserUpdateWhenDataIsValid() {
        Mockito.when(userRepository.findByEmail(existingEmail)).thenReturn(user);

        UserDTO result = userService.updateMe(userUpdateDTO);

        Assertions.assertNotNull(result);
        Assertions.assertEquals("Maria White", result.getName());
        Assertions.assertEquals("mariawhite@gmail.com", result.getEmail());
    }

    @Test
    public void updateMeShouldReturnUsernameNotFoundExceptionWhenDataDoesNotValid() {
        SecurityContextHolder.clearContext();

        Assertions.assertThrows(UsernameNotFoundException.class, () -> {
            userService.updateMe(userUpdateDTO);
        });
    }
}
