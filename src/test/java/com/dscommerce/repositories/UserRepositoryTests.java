package com.dscommerce.repositories;

import com.dscommerce.entities.User;
import com.dscommerce.projections.UserDetailsProjection;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

@DataJpaTest
public class UserRepositoryTests {

    private String existingEmail, nonExistingEmail;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() throws Exception {
        existingEmail = "maria@gmail.com";
        nonExistingEmail = "nonemailexisting@gmail.com";
    }

    @Test
    public void findByEmailShouldReturnNotNullWhenEmailExists() {
        User user = userRepository.findByEmail(existingEmail);
        Assertions.assertNotNull(user);
    }

    @Test
    public void findByEmailShouldReturnNullWhenEmailDoesNotExists() {
        User user = userRepository.findByEmail(nonExistingEmail);
        Assertions.assertNull(user);
    }

    @Test
    public void searchUserAndRolesByEmailShouldReturnNotNullWhenEmailExists() {
        List<UserDetailsProjection> result = userRepository.searchUserAndRolesByEmail(existingEmail);

        Assertions.assertFalse(result.isEmpty());
        Assertions.assertEquals(existingEmail, result.getFirst().getUsername());
    }

    @Test
    public void searchUserAndRolesByEmailShouldReturnEmptyListWhenEmailDoesNotExists() {
        List<UserDetailsProjection> result = userRepository.searchUserAndRolesByEmail(nonExistingEmail);

        Assertions.assertTrue(result.isEmpty());
    }
}
