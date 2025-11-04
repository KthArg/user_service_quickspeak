package com.yourteacher.userservice.domain.port.out;

/**
 * Puerto de salida para encriptación de contraseñas
 * Abstrae la implementación de encriptación del dominio
 */
public interface PasswordEncoder {
    
    /**
     * Encripta una contraseña en texto plano
     */
    String encode(String rawPassword);
    
    /**
     * Verifica si una contraseña en texto plano coincide con su versión encriptada
     */
    boolean matches(String rawPassword, String encodedPassword);
}
