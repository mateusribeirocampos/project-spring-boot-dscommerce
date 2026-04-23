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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
public class PasswordRecoveryService {

    @Value("${app.email.resetPasswordUri}")
    private String resetPasswordUri;

    @Autowired
    private EmailFactory emailFactory;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordRecoveryRepository passwordRecoveryRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public void createRecoveryToken(ForgotPasswordDTO dto) {
        User user = userRepository.findByEmail(dto.getEmail());
        if (user == null) {
            return;
        }

        String token = UUID.randomUUID().toString();
        Instant expiration = Instant.now().plus(10, ChronoUnit.MINUTES);

        PasswordRecovery passwordRecovery = new PasswordRecovery();
        passwordRecovery.setToken(token);
        passwordRecovery.setUser(user);
        passwordRecovery.setExpiryDate(expiration);

        passwordRecoveryRepository.save(passwordRecovery);

        String link = resetPasswordUri + token;
        emailService.plainTextEmail(emailFactory.buildResetTokenEmail(link, user));
    }

    @Transactional
    public void saveNewPassword(NewPasswordDTO dto) {
        PasswordRecovery passwordRecovery = passwordRecoveryRepository
                .findByToken(dto.getToken())
                .orElseThrow(() -> new ResourceNotFoundException("Invalid token"));

        if (passwordRecovery.isUsed()) throw new InvalidTokenException("Token already used");
        if (passwordRecovery.getExpiryDate().isBefore(Instant.now())) throw new InvalidTokenException("Token expired");

        User user = passwordRecovery.getUser();
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        passwordRecovery.setUsed(true);

        userRepository.save(user);
        passwordRecoveryRepository.save(passwordRecovery);
    }
}
