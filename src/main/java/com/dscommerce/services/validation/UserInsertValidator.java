package com.dscommerce.services.validation;

import com.dscommerce.dto.UserInsertDTO;
import com.dscommerce.dto.exceptions.FieldMessage;
import com.dscommerce.entities.User;
import com.dscommerce.repositories.UserRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

public class UserInsertValidator implements ConstraintValidator<UserInsertValid, UserInsertDTO> {

    @Autowired
    private UserRepository userRepository;

    @Override
    public void initialize(UserInsertValid constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(UserInsertDTO dto, ConstraintValidatorContext constraintValidatorContext) {

        List<FieldMessage> fieldMessageList = new ArrayList<>();

        User user = userRepository.findByEmail(dto.getEmail());
        if (user != null) {
            fieldMessageList.add(new FieldMessage("email", "the email already exists"));
        }

        for (FieldMessage e : fieldMessageList) {
            constraintValidatorContext.disableDefaultConstraintViolation();
            constraintValidatorContext
                    .buildConstraintViolationWithTemplate(e.getMessage())
                    .addPropertyNode(e.getFieldName())
                    .addConstraintViolation();
        }
        return fieldMessageList.isEmpty();
    }
}
