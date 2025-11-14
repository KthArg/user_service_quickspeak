package com.yourteacher.userservice.domain.port.in;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Caso de uso para login vía OAuth (Google, Facebook, etc.)
 * Domain Layer - Input Port
 */
public interface OAuthLoginUseCase {

    /**
     * Autentica o registra un usuario mediante OAuth
     * Si el usuario no existe, lo crea automáticamente
     * Si el usuario existe, lo actualiza con la información de OAuth
     *
     * @param request Datos del usuario obtenidos del proveedor OAuth
     * @return Respuesta con token JWT y datos del usuario
     */
    OAuthLoginResponse loginWithOAuth(OAuthLoginRequest request);

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    class OAuthLoginRequest {
        private String email;
        private String firstName;
        private String lastName;
        private String provider;      // "google", "facebook", etc.
        private String providerId;    // ID del usuario en el proveedor
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    class OAuthLoginResponse {
        private String token;
        private Long userId;
        private String email;
        private String firstName;
        private String lastName;
        private boolean isNewUser;    // true si el usuario fue creado, false si ya existía
    }
}
