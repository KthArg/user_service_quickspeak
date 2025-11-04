package com.yourteacher.userservice.domain.model;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;

/**
 * Entidad de dominio Language (Hexagonal Architecture - Domain Layer)
 * Representa un idioma disponible en la plataforma de aprendizaje
 *
 * Esta clase es inmutable y contiene la lógica de negocio relacionada
 * con los idiomas soportados por el sistema.
 */
@Value
@Builder
public class Language {

    Long id;
    String name;
    String code;
    String flagUrl;
    LocalDateTime createdAt;

    /**
     * Valida que el nombre del idioma sea válido
     * @return true si el nombre no es null ni vacío
     */
    public boolean hasValidName() {
        return name != null && !name.trim().isEmpty();
    }

    /**
     * Valida que el código del idioma cumpla con el formato ISO 639-1
     * @return true si el código tiene exactamente 2 caracteres en minúsculas
     */
    public boolean hasValidCode() {
        return code != null && code.matches("^[a-z]{2}$");
    }

    /**
     * Valida que todos los campos requeridos estén presentes y sean válidos
     * @return true si el idioma es válido
     */
    public boolean isValid() {
        return hasValidName() && hasValidCode();
    }

    /**
     * Factory method para crear un nuevo idioma con validaciones
     *
     * @param name Nombre del idioma (ej: "Spanish", "English")
     * @param code Código ISO 639-1 de 2 letras en minúsculas (ej: "es", "en")
     * @param flagUrl URL de la imagen de la bandera del idioma
     * @return Una nueva instancia de Language
     * @throws IllegalArgumentException si las validaciones fallan
     */
    public static Language create(String name, String code, String flagUrl) {
        // Normalizar el código a minúsculas
        String normalizedCode = code != null ? code.toLowerCase().trim() : null;

        Language language = Language.builder()
                .name(name != null ? name.trim() : null)
                .code(normalizedCode)
                .flagUrl(flagUrl)
                .createdAt(LocalDateTime.now())
                .build();

        // Validar
        if (!language.hasValidName()) {
            throw new IllegalArgumentException("El nombre del idioma no puede estar vacío");
        }

        if (!language.hasValidCode()) {
            throw new IllegalArgumentException(
                "El código del idioma debe tener exactamente 2 caracteres en minúsculas (ISO 639-1). Recibido: " + code
            );
        }

        return language;
    }

    /**
     * Crea una nueva instancia de Language con un ID asignado
     * Útil cuando se persiste y se necesita asignar el ID generado
     *
     * @param id El ID del idioma
     * @return Una nueva instancia con el ID asignado
     */
    public Language withId(Long id) {
        return Language.builder()
                .id(id)
                .name(this.name)
                .code(this.code)
                .flagUrl(this.flagUrl)
                .createdAt(this.createdAt)
                .build();
    }

    /**
     * Crea una nueva instancia con una URL de bandera actualizada
     *
     * @param newFlagUrl La nueva URL de la bandera
     * @return Una nueva instancia con la URL actualizada
     */
    public Language withFlagUrl(String newFlagUrl) {
        return Language.builder()
                .id(this.id)
                .name(this.name)
                .code(this.code)
                .flagUrl(newFlagUrl)
                .createdAt(this.createdAt)
                .build();
    }
}
