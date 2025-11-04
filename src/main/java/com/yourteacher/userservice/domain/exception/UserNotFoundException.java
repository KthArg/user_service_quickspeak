package com.yourteacher.userservice.domain.exception;

/**
 * Excepción lanzada cuando se intenta acceder a un usuario que no existe
 * (Hexagonal Architecture - Domain Layer)
 *
 * Esta excepción indica una violación de regla de negocio:
 * el usuario referenciado debe existir en el sistema.
 */
public class UserNotFoundException extends DomainException {

    private final Long userId;

    /**
     * Constructor con ID del usuario no encontrado
     *
     * @param userId ID del usuario que no fue encontrado
     */
    public UserNotFoundException(Long userId) {
        super(String.format("Usuario con ID %d no encontrado", userId));
        this.userId = userId;
    }

    /**
     * Constructor con ID del usuario y causa
     *
     * @param userId ID del usuario que no fue encontrado
     * @param cause Excepción que causó este error
     */
    public UserNotFoundException(Long userId, Throwable cause) {
        super(String.format("Usuario con ID %d no encontrado", userId), cause);
        this.userId = userId;
    }

    /**
     * Obtiene el ID del usuario que no fue encontrado
     *
     * @return ID del usuario
     */
    public Long getUserId() {
        return userId;
    }
}
