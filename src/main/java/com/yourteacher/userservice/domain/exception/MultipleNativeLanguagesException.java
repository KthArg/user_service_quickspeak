package com.yourteacher.userservice.domain.exception;

/**
 * Excepción lanzada cuando se intenta asignar múltiples idiomas nativos a un usuario
 * (Hexagonal Architecture - Domain Layer)
 *
 * Esta excepción indica una violación de regla de negocio:
 * un usuario solo puede tener UN idioma marcado como nativo.
 */
public class MultipleNativeLanguagesException extends DomainException {

    private final Long userId;

    /**
     * Constructor con ID del usuario
     *
     * @param userId ID del usuario que ya tiene un idioma nativo
     */
    public MultipleNativeLanguagesException(Long userId) {
        super(String.format(
            "El usuario con ID %d ya tiene un idioma nativo. Solo se permite un idioma nativo por usuario.",
            userId
        ));
        this.userId = userId;
    }

    /**
     * Constructor con ID del usuario y causa
     *
     * @param userId ID del usuario que ya tiene un idioma nativo
     * @param cause Excepción que causó este error
     */
    public MultipleNativeLanguagesException(Long userId, Throwable cause) {
        super(String.format(
            "El usuario con ID %d ya tiene un idioma nativo. Solo se permite un idioma nativo por usuario.",
            userId
        ), cause);
        this.userId = userId;
    }

    /**
     * Obtiene el ID del usuario
     *
     * @return ID del usuario
     */
    public Long getUserId() {
        return userId;
    }
}
