package com.dscommerce.services.validation;

import com.dscommerce.dto.UserUpdateDTO;
import com.dscommerce.dto.exceptions.FieldMessage;
import com.dscommerce.entities.User;
import com.dscommerce.repositories.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerMapping;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class UserUpdateValidator implements ConstraintValidator<UserUpdateValid, UserUpdateDTO> {

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private UserRepository userRepository;

    @Override
    public void initialize(UserUpdateValid constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(UserUpdateDTO dto, ConstraintValidatorContext constraintValidatorContext) {

        @SuppressWarnings("unchecked")
        var uriVars = (Map<String, String>) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
        long userId = Long.parseLong(uriVars.get("id"));

        List<FieldMessage> fieldMessageList = new ArrayList<>();

        User user = userRepository.findByEmail(dto.getEmail());
        if (user != null && userId != user.getId()) {
            fieldMessageList.add(new FieldMessage("email", "the email already exists"));
        }

        for (FieldMessage e : fieldMessageList) {
            constraintValidatorContext.disableDefaultConstraintViolation();
            constraintValidatorContext.buildConstraintViolationWithTemplate(e.getMessage())
                    .addPropertyNode(e.getFieldName())
                    .addConstraintViolation();
        }
        return fieldMessageList.isEmpty();
    }
}
