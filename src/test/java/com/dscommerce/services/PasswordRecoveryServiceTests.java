package com.dscommerce.services;

import com.dscommerce.dto.ForgotPasswordDTO;
import com.dscommerce.dto.NewPasswordDTO;
import com.dscommerce.entities.PasswordRecovery;
import com.dscommerce.entities.User;
import com.dscommerce.repositories.PasswordRecoveryRepository;
import com.dscommerce.repositories.UserRepository;
import com.dscommerce.services.email.EmailFactory;
import com.dscommerce.services.email.EmailService;
import com.dscommerce.services.exceptions.InvalidTokenException;
import com.dscommerce.services.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class PasswordRecoveryServiceTests {

    @InjectMocks
    private PasswordRecoveryService passwordRecoveryService;

    @Mock
    private EmailFactory emailFactory;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordRecoveryRepository passwordRecoveryRepository;

    @Mock
    private EmailService emailService;

    @Mock
    private PasswordEncoder passwordEncoder;

    private ForgotPasswordDTO forgotPasswordDTO;
    private NewPasswordDTO newPasswordDTO;
    private PasswordRecovery passwordRecovery;

    @BeforeEach
    void setUp() throws Exception {

    }

    @Test
    public void shouldReturnCreateRecoveryTokenWhenForgotPassword() {
        User user = new User();
        forgotPasswordDTO = new ForgotPasswordDTO("email@test.com");
        Mockito.when(userRepository.findByEmail(forgotPasswordDTO.getEmail())).thenReturn(user);

        ArgumentCaptor<PasswordRecovery> captor = ArgumentCaptor.forClass(PasswordRecovery.class);
        passwordRecoveryService.createRecoveryToken(forgotPasswordDTO);
        Mockito.verify(passwordRecoveryRepository).save(captor.capture());
    }

    @Test
    public void shouldReturnEmptyWhenUserIsNull() {
        forgotPasswordDTO = new ForgotPasswordDTO("email@test.com");
        Mockito.when(userRepository.findByEmail(forgotPasswordDTO.getEmail())).thenReturn(null);

        passwordRecoveryService.createRecoveryToken(forgotPasswordDTO);
        Mockito.verify(passwordRecoveryRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    public void shouldReturnSaveNewPasswordWhenTokenIsValid() {
        newPasswordDTO = new NewPasswordDTO("123", "123456789");
        passwordRecovery = new PasswordRecovery();
        passwordRecovery.setUser(new User());
        passwordRecovery.setExpiryDate(Instant.now().plus(10, ChronoUnit.MINUTES));
        Mockito.when(passwordRecoveryRepository.findByToken(newPasswordDTO.getToken()))
                .thenReturn(Optional.of(passwordRecovery));
        ArgumentCaptor<PasswordRecovery> captor = ArgumentCaptor.forClass(PasswordRecovery.class);
        passwordRecoveryService.saveNewPassword(newPasswordDTO);
        Mockito.verify(passwordRecoveryRepository).save(captor.capture());

    }

    @Test
    public void shouldReturnInvalidTokenWhenTokenIsNotValid() {
        newPasswordDTO = new NewPasswordDTO("", "123456789");
        Mockito.when(passwordRecoveryRepository.findByToken(newPasswordDTO.getToken()))
                .thenReturn(Optional.empty());

        Assertions.assertThrows(ResourceNotFoundException.class,
                () -> passwordRecoveryService.saveNewPassword(newPasswordDTO));
    }

    @Test
    public void shouldReturnInvalidTokenExceptionWhenTokenIsUsed() {
        newPasswordDTO = new NewPasswordDTO("123", "123456789");
        passwordRecovery = new PasswordRecovery();
        passwordRecovery.setUsed(true);
        passwordRecovery.setExpiryDate(Instant.now().plus(10, ChronoUnit.MINUTES));
        Mockito.when(passwordRecoveryRepository.findByToken(newPasswordDTO.getToken()))
                .thenReturn(Optional.of(passwordRecovery));

        Assertions.assertThrows(InvalidTokenException.class,
                () -> passwordRecoveryService.saveNewPassword(newPasswordDTO));
    }

    @Test
    public void shouldReturnInvalidTokenExceptionWhenDateIsExpired() {
        newPasswordDTO = new NewPasswordDTO("123", "123456789");
        passwordRecovery = new PasswordRecovery();
        passwordRecovery.setExpiryDate(Instant.now().minus(10, ChronoUnit.MINUTES));
        Mockito.when(passwordRecoveryRepository.findByToken(newPasswordDTO.getToken()))
                .thenReturn(Optional.of(passwordRecovery));

        Assertions.assertThrows(InvalidTokenException.class,
                () -> passwordRecoveryService.saveNewPassword(newPasswordDTO));
    }
}
