package com.yourteacher.userservice.domain.exception;

/**
 * Excepción lanzada cuando se intenta operar sobre un idioma que el usuario no tiene agregado
 * (Hexagonal Architecture - Domain Layer)
 *
 * Esta excepción indica una violación de regla de negocio:
 * ciertas operaciones requieren que el usuario ya tenga el idioma agregado.
 * Por ejemplo, no se puede marcar un idioma como nativo si el usuario no lo tiene.
 */
public class LanguageNotAddedToUserException extends DomainException {

    private final Long userId;
    private final Long languageId;

    /**
     * Constructor con IDs del usuario e idioma
     *
     * @param userId ID del usuario
     * @param languageId ID del idioma que no está agregado
     */
    public LanguageNotAddedToUserException(Long userId, Long languageId) {
        super(String.format(
            "El usuario con ID %d no tiene agregado el idioma con ID %d. " +
            "Primero debe agregar el idioma antes de realizar esta operación.",
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
     * @param languageId ID del idioma que no está agregado
     * @param cause Excepción que causó este error
     */
    public LanguageNotAddedToUserException(Long userId, Long languageId, Throwable cause) {
        super(String.format(
            "El usuario con ID %d no tiene agregado el idioma con ID %d. " +
            "Primero debe agregar el idioma antes de realizar esta operación.",
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
     * Obtiene el ID del idioma que no está agregado
     *
     * @return ID del idioma
     */
    public Long getLanguageId() {
        return languageId;
    }
}
