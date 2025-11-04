package com.yourteacher.userservice.domain.exception;

/**
 * Excepción lanzada cuando se intenta acceder a un idioma que no existe
 * (Hexagonal Architecture - Domain Layer)
 *
 * Esta excepción indica una violación de regla de negocio:
 * el idioma referenciado debe existir en el catálogo del sistema.
 */
public class LanguageNotFoundException extends DomainException {

    private final Long languageId;

    /**
     * Constructor con ID del idioma no encontrado
     *
     * @param languageId ID del idioma que no fue encontrado
     */
    public LanguageNotFoundException(Long languageId) {
        super(String.format("Idioma con ID %d no encontrado", languageId));
        this.languageId = languageId;
    }

    /**
     * Constructor con código del idioma no encontrado
     *
     * @param languageCode Código ISO del idioma que no fue encontrado
     */
    public LanguageNotFoundException(String languageCode) {
        super(String.format("Idioma con código '%s' no encontrado", languageCode));
        this.languageId = null;
    }

    /**
     * Constructor con ID del idioma y causa
     *
     * @param languageId ID del idioma que no fue encontrado
     * @param cause Excepción que causó este error
     */
    public LanguageNotFoundException(Long languageId, Throwable cause) {
        super(String.format("Idioma con ID %d no encontrado", languageId), cause);
        this.languageId = languageId;
    }

    /**
     * Obtiene el ID del idioma que no fue encontrado
     *
     * @return ID del idioma, o null si la búsqueda fue por código
     */
    public Long getLanguageId() {
        return languageId;
    }
}
