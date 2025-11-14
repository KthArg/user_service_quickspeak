package com.yourteacher.userservice.domain.port.in;

import com.yourteacher.userservice.domain.model.User;
import com.yourteacher.userservice.domain.model.UserLanguage;

import java.util.List;

/**
 * Puerto de entrada (Input Port) para obtener el perfil completo de un usuario
 * Define el caso de uso para obtener datos del usuario con sus idiomas
 * (Hexagonal Architecture - Primary Port)
 *
 * Este caso de uso permite:
 * - Obtener datos completos del usuario
 * - Incluir lista de idiomas del usuario
 * - Formatear respuesta con toda la información necesaria
 */
public interface GetUserProfileUseCase {

    /**
     * Obtiene el perfil completo de un usuario por su ID
     *
     * Precondiciones:
     * - El usuario debe existir en el sistema
     *
     * @param userId ID del usuario
     * @return UserProfile con datos del usuario y sus idiomas
     * @throws com.yourteacher.userservice.domain.exception.UserNotFoundException si el usuario no existe
     */
    UserProfile getUserProfile(Long userId);

    /**
     * Clase interna para la respuesta del perfil de usuario
     */
    class UserProfile {
        private final User user;
        private final List<UserLanguage> languages;

        public UserProfile(User user, List<UserLanguage> languages) {
            this.user = user;
            this.languages = languages;
        }

        public User getUser() {
            return user;
        }

        public List<UserLanguage> getLanguages() {
            return languages;
        }
    }
}
