package com.yourteacher.userservice.domain.port.out;

import com.yourteacher.userservice.domain.model.User;

/**
 * Puerto de salida para generación y validación de JWT tokens
 * Abstrae la implementación de JWT del dominio
 */
public interface JwtTokenProvider {

    /**
     * Genera un JWT token para un usuario
     * @param user Usuario para el cual generar el token
     * @return JWT token generado
     */
    String generateToken(User user);

    /**
     * Valida un JWT token
     * @param token Token JWT a validar
     * @return true si el token es válido, false en caso contrario
     */
    boolean validateToken(String token);

    /**
     * Extrae el email del usuario desde un JWT token
     * @param token Token JWT del cual extraer el email
     * @return Email del usuario
     */
    String getEmailFromToken(String token);

    /**
     * Extrae el ID del usuario desde un JWT token
     * @param token Token JWT del cual extraer el ID
     * @return ID del usuario
     */
    Long getUserIdFromToken(String token);
}
