package com.yourteacher.userservice.application.service;

import com.yourteacher.userservice.domain.exception.UserNotFoundException;
import com.yourteacher.userservice.domain.model.User;
import com.yourteacher.userservice.domain.model.UserLanguage;
import com.yourteacher.userservice.domain.port.in.GetUserProfileUseCase;
import com.yourteacher.userservice.domain.port.out.UserLanguageRepository;
import com.yourteacher.userservice.domain.port.out.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implementación del caso de uso para obtener perfil de usuario (Application Layer)
 * Orquesta la obtención de datos de usuario con sus idiomas
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class GetUserProfileService implements GetUserProfileUseCase {

    private final UserRepository userRepository;
    private final UserLanguageRepository userLanguageRepository;

    @Override
    @Transactional(readOnly = true)
    public UserProfile getUserProfile(Long userId) {
        log.debug("Getting profile for user ID: {}", userId);

        // Obtener usuario
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        // Obtener idiomas del usuario
        List<UserLanguage> languages = userLanguageRepository.findByUserId(userId);

        log.debug("Found user {} with {} languages", user.getEmail(), languages.size());

        return new UserProfile(user, languages);
    }
}
