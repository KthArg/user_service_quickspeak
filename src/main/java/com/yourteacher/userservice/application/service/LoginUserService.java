package com.yourteacher.userservice.application.service;

import com.yourteacher.userservice.domain.exception.InvalidCredentialsException;
import com.yourteacher.userservice.domain.model.User;
import com.yourteacher.userservice.domain.port.in.LoginUseCase;
import com.yourteacher.userservice.domain.port.out.JwtTokenProvider;
import com.yourteacher.userservice.domain.port.out.PasswordEncoder;
import com.yourteacher.userservice.domain.port.out.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Implementación del caso de uso de Login (Application Layer)
 * Contiene la lógica de autenticación y generación de tokens JWT
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class LoginUserService implements LoginUseCase {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    @Transactional(readOnly = true)
    public LoginResponse login(String email, String password) {
        log.debug("Attempting login for email: {}", email);

        // Buscar usuario por email
        Optional<User> userOptional = userRepository.findByEmail(email);

        // Validar que el usuario existe y está activo
        if (userOptional.isEmpty()) {
            log.warn("Login failed: user not found for email {}", email);
            throw new InvalidCredentialsException(email);
        }

        User user = userOptional.get();

        if (!user.isActive()) {
            log.warn("Login failed: user {} is inactive", email);
            throw new InvalidCredentialsException(email);
        }

        // Validar password
        if (!passwordEncoder.matches(password, user.getPassword())) {
            log.warn("Login failed: invalid password for email {}", email);
            throw new InvalidCredentialsException(email);
        }

        // Generar JWT token
        String token = jwtTokenProvider.generateToken(user);

        log.info("Login successful for user: {}", email);

        // Retornar respuesta con token y datos del usuario
        return new LoginResponse(
                token,
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName()
        );
    }
}
