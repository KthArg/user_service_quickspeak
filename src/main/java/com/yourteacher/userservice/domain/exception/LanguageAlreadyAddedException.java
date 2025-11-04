package com.yourteacher.userservice.domain.exception;

/**
 * Excepción lanzada cuando se intenta agregar un idioma que el usuario ya tiene
 * (Hexagonal Architecture - Domain Layer)
 *
 * Esta excepción indica una violación de regla de negocio:
 * un usuario no puede tener el mismo idioma duplicado en su lista.
 */
public class LanguageAlreadyAddedException extends DomainException {

    private final Long userId;
    private final Long languageId;

    /**
     * Constructor con IDs del usuario e idioma
     *
     * @param userId ID del usuario
     * @param languageId ID del idioma que ya está agregado
     */
    public LanguageAlreadyAddedException(Long userId, Long languageId) {
        super(String.format(
            "El usuario con ID %d ya tiene agregado el idioma con ID %d",
            userId,
            languageId
        ));
        this.userId = userId;
        this.languageId = languageId;
    }

    /**
     * Constructor con IDs del usuario e idioma y causa
     *
     * @param userId ID del usuario
     * @param languageId ID del idioma que ya está agregado
     * @param cause Excepción que causó este error
     */
    public LanguageAlreadyAddedException(Long userId, Long languageId, Throwable cause) {
        super(String.format(
            "El usuario con ID %d ya tiene agregado el idioma con ID %d",
            userId,
            languageId
        ), cause);
        this.userId = userId;
        this.languageId = languageId;
    }

    /**
     * Obtiene el ID del usuario
     *
     * @return ID del usuario
     */
    public Long getUserId() {
        return userId;
    }

    /**
     * Obtiene el ID del idioma que ya estaba agregado
     *
     * @return ID del idioma
     */
    public Long getLanguageId() {
        return languageId;
    }
}
