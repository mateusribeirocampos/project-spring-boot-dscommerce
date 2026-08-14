package com.dscommerce.services.validation;

import com.dscommerce.dto.UserUpdateDTO;
import com.dscommerce.entities.User;
import com.dscommerce.repositories.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.servlet.HandlerMapping;

import java.util.Map;

@ExtendWith(MockitoExtension.class)
public class UserUpdateValidatorTests {

    @InjectMocks
    private UserUpdateValidator validator;

    @Mock
    private HttpServletRequest request;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ConstraintValidatorContext constraintValidatorContext;


    @BeforeEach
    void setUp() throws Exception {
    }

    @Test
    public void shouldReturnTrueWhenAdminUpdateUserAndEmailIsNotTaken() {
        UserUpdateDTO dto = new UserUpdateDTO();
        dto.setEmail("new-email@gmail.com");

        Mockito.when(request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE))
                .thenReturn(Map.of("id", "5"));
        Mockito.when(userRepository.findByEmail(dto.getEmail())).thenReturn(null);
        boolean result = validator.isValid(dto, constraintValidatorContext);

        Assertions.assertTrue(result);
    }

    @Test
    public void shouldReturnFalseWhenAdminUpdateUserAndEmailIsTaken() {
        UserUpdateDTO dto = new UserUpdateDTO();
        dto.setEmail("maria@gmail.com");

        User outherUser = new User();
        outherUser.setId(2L);

        Mockito.when(request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE))
                .thenReturn(Map.of("id", "1"));
        Mockito.when(userRepository.findByEmail(dto.getEmail())).thenReturn(outherUser);
        ConstraintValidatorContext.ConstraintViolationBuilder builder =
                Mockito.mock(ConstraintValidatorContext.ConstraintViolationBuilder.class);
        Mockito.when(constraintValidatorContext.buildConstraintViolationWithTemplate(Mockito.anyString()))
                .thenReturn(builder);

        ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderCustomizableContext nodeBuilder =
                Mockito.mock(ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderCustomizableContext.class);
        Mockito.when(builder.addPropertyNode(Mockito.anyString())).thenReturn(nodeBuilder);

        boolean result = validator.isValid(dto, constraintValidatorContext);

        Assertions.assertFalse(result);
    }
}
