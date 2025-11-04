package com.yourteacher.userservice.domain.exception;

/**
 * Excepción lanzada cuando se intenta eliminar el idioma nativo de un usuario
 * (Hexagonal Architecture - Domain Layer)
 *
 * Esta excepción indica una violación de regla de negocio:
 * un usuario no puede eliminar su idioma nativo sin cambiarlo primero.
 */
public class NativeLanguageCannotBeRemovedException extends DomainException {

    private final Long userId;
    private final Long languageId;

    /**
     * Constructor con IDs del usuario e idioma
     *
     * @param userId ID del usuario
     * @param languageId ID del idioma nativo que se intentó eliminar
     */
    public NativeLanguageCannotBeRemovedException(Long userId, Long languageId) {
        super(String.format(
            "No se puede eliminar el idioma nativo (ID %d) del usuario con ID %d. " +
            "Primero debe establecer otro idioma como nativo.",
            languageId,
            userId
        ));
        this.userId = userId;
        this.languageId = languageId;
    }

    /**
     * Constructor con IDs del usuario e idioma y causa
     *
     * @param userId ID del usuario
     * @param languageId ID del idioma nativo que se intentó eliminar
     * @param cause Excepción que causó este error
     */
    public NativeLanguageCannotBeRemovedException(Long userId, Long languageId, Throwable cause) {
        super(String.format(
            "No se puede eliminar el idioma nativo (ID %d) del usuario con ID %d. " +
            "Primero debe establecer otro idioma como nativo.",
            languageId,
            userId
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
     * Obtiene el ID del idioma nativo que se intentó eliminar
     *
     * @return ID del idioma
     */
    public Long getLanguageId() {
        return languageId;
    }
}
