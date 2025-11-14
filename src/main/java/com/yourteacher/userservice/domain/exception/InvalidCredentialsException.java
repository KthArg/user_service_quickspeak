package com.yourteacher.userservice.domain.exception;

/**
 * Excepción lanzada cuando las credenciales de login son inválidas
 * (Hexagonal Architecture - Domain Layer)
 *
 * Esta excepción indica una violación de regla de negocio:
 * el email o password proporcionados son incorrectos, o el usuario está inactivo.
 */
public class InvalidCredentialsException extends DomainException {

    private final String email;

    /**
     * Constructor con email del usuario que intentó autenticarse
     *
     * @param email Email utilizado en el intento de login
     */
    public InvalidCredentialsException(String email) {
        super(String.format("Credenciales inválidas para el email: %s", email));
        this.email = email;
    }

    /**
     * Constructor con email y causa
     *
     * @param email Email utilizado en el intento de login
     * @param cause Excepción que causó este error
     */
    public InvalidCredentialsException(String email, Throwable cause) {
        super(String.format("Credenciales inválidas para el email: %s", email), cause);
        this.email = email;
    }

    /**
     * Obtiene el email que se intentó utilizar
     *
     * @return Email del intento de login
     */
    public String getEmail() {
        return email;
    }
}
