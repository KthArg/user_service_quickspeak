package com.yourteacher.userservice.domain.model;

/**
 * Enum que representa el proveedor de autenticación del usuario
 */
public enum AuthProvider {
    /**
     * Autenticación local con email y password
     */
    LOCAL,

    /**
     * Autenticación con Google OAuth/EasyAuth
     */
    GOOGLE,

    /**
     * Autenticación con Microsoft OAuth/EasyAuth
     */
    MICROSOFT,

    /**
     * Autenticación con Facebook OAuth/EasyAuth
     */
    FACEBOOK
}
