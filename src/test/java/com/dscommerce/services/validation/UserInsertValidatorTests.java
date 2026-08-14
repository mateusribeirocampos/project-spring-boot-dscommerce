package com.dscommerce.services.validation;

import com.dscommerce.dto.UserInsertDTO;
import com.dscommerce.entities.User;
import com.dscommerce.repositories.UserRepository;
import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class UserInsertValidatorTests {

    @InjectMocks
    private UserInsertValidator insertValidator;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ConstraintValidatorContext constraintValidatorContext;


    @BeforeEach
    void setUp() throws Exception {
    }

    @Test
    public void shouldReturnTrueWhenAdminInsertUserAndEmailIsNotTaken() {
        UserInsertDTO dto = new UserInsertDTO();
        dto.setEmail("new-email@gmail.com");

        Mockito.when(userRepository.findByEmail(dto.getEmail())).thenReturn(null);
        boolean result = insertValidator.isValid(dto, constraintValidatorContext);

        Assertions.assertTrue(result);
    }

    @Test
    public void shouldReturnFalseWhenAdminInsertUserAndEmailIsTaken() {
        UserInsertDTO dto = new UserInsertDTO();
        dto.setEmail("maria@gmail.com");

        User outherUser = new User();
        outherUser.setId(2L);

        Mockito.when(userRepository.findByEmail(dto.getEmail())).thenReturn(outherUser);
        ConstraintValidatorContext.ConstraintViolationBuilder builder =
                Mockito.mock(ConstraintValidatorContext.ConstraintViolationBuilder.class);
        Mockito.when(constraintValidatorContext.buildConstraintViolationWithTemplate(Mockito.anyString()))
                .thenReturn(builder);

        ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderCustomizableContext nodeBuilder =
                Mockito.mock(ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderCustomizableContext.class);
        Mockito.when(builder.addPropertyNode(Mockito.anyString())).thenReturn(nodeBuilder);

        boolean result = insertValidator.isValid(dto, constraintValidatorContext);

        Assertions.assertFalse(result);
    }
}
