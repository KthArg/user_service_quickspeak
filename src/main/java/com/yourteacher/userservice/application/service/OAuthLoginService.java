package com.yourteacher.userservice.application.service;

import com.yourteacher.userservice.domain.model.Role;
import com.yourteacher.userservice.domain.model.User;
import com.yourteacher.userservice.domain.model.UserStatus;
import com.yourteacher.userservice.domain.port.in.OAuthLoginUseCase;
import com.yourteacher.userservice.domain.port.out.JwtTokenProvider;
import com.yourteacher.userservice.domain.port.out.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * Servicio de aplicación para login OAuth
 * Application Layer - Service
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class OAuthLoginService implements OAuthLoginUseCase {

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    @Transactional
    public OAuthLoginResponse loginWithOAuth(OAuthLoginRequest request) {
        log.info("OAuth login attempt for email: {} via provider: {}", request.getEmail(), request.getProvider());

        // Buscar usuario existente por email
        Optional<User> existingUser = userRepository.findByEmail(request.getEmail());

        User user;
        boolean isNewUser;

        if (existingUser.isPresent()) {
            // Usuario existe - actualizar información si es necesario
            user = existingUser.get();
            isNewUser = false;

            log.info("Existing user found: {}", user.getEmail());

            // Actualizar nombre si cambió en el proveedor OAuth
            boolean updated = false;
            if (!user.getFirstName().equals(request.getFirstName()) ||
                !user.getLastName().equals(request.getLastName())) {

                // Reconstruir usuario con información actualizada
                user = User.builder()
                        .id(user.getId())
                        .email(user.getEmail())
                        .password(user.getPassword())
                        .firstName(request.getFirstName())
                        .lastName(request.getLastName())
                        .avatarSeed(user.getAvatarSeed())
                        .roles(user.getRoles())
                        .status(user.getStatus())
                        .createdAt(user.getCreatedAt())
                        .updatedAt(LocalDateTime.now())
                        .build();

                user = userRepository.save(user);
                updated = true;
                log.info("Updated user information from OAuth provider");
            }
        } else {
            // Usuario nuevo - crear cuenta
            log.info("Creating new user from OAuth: {}", request.getEmail());

            user = User.builder()
                    .email(request.getEmail())
                    .firstName(request.getFirstName())
                    .lastName(request.getLastName())
                    // Para OAuth no tenemos password, generamos uno aleatorio pero no se usará
                    .password(UUID.randomUUID().toString())
                    .roles(Set.of(Role.STUDENT))
                    .status(UserStatus.ACTIVE)
                    .avatarSeed(UUID.randomUUID().toString())
                    .createdAt(LocalDateTime.now())
                    .build();

            user = userRepository.save(user);
            isNewUser = true;

            log.info("New user created via OAuth: {} (ID: {})", user.getEmail(), user.getId());
        }

        // Generar token JWT
        String token = jwtTokenProvider.generateToken(user);
        log.info("JWT token generated for user: {}", user.getEmail());

        return OAuthLoginResponse.builder()
                .token(token)
                .userId(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .isNewUser(isNewUser)
                .build();
    }
}
