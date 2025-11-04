package com.yourteacher.userservice.domain.model;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;

/**
 * Entidad de dominio UserLanguage (Hexagonal Architecture - Domain Layer)
 * Representa la relación entre un usuario y un idioma en la plataforma
 *
 * Esta clase es inmutable y contiene la lógica de negocio relacionada
 * con los idiomas que un usuario está aprendiendo o habla nativamente.
 */
@Value
@Builder
public class UserLanguage {

    Long id;
    Long userId;
    Long languageId;
    boolean isNative;
    LocalDateTime addedAt;

    /**
     * Verifica si este idioma es nativo del usuario
     * @return true si es idioma nativo
     */
    public boolean isNativeLanguage() {
        return isNative;
    }

    /**
     * Verifica si este idioma está siendo aprendido por el usuario
     * @return true si NO es idioma nativo (está aprendiéndolo)
     */
    public boolean isLearningLanguage() {
        return !isNative;
    }

    /**
     * Marca este idioma como nativo del usuario
     * Retorna una nueva instancia con isNative=true (inmutabilidad)
     *
     * @return Una nueva instancia de UserLanguage marcada como nativa
     */
    public UserLanguage markAsNative() {
        return UserLanguage.builder()
                .id(this.id)
                .userId(this.userId)
                .languageId(this.languageId)
                .isNative(true)
                .addedAt(this.addedAt)
                .build();
    }

    /**
     * Marca este idioma como idioma de aprendizaje del usuario
     * Retorna una nueva instancia con isNative=false (inmutabilidad)
     *
     * @return Una nueva instancia de UserLanguage marcada como idioma de aprendizaje
     */
    public UserLanguage markAsLearning() {
        return UserLanguage.builder()
                .id(this.id)
                .userId(this.userId)
                .languageId(this.languageId)
                .isNative(false)
                .addedAt(this.addedAt)
                .build();
    }

    /**
     * Factory method para crear una nueva relación usuario-idioma
     *
     * @param userId ID del usuario
     * @param languageId ID del idioma
     * @param isNative true si es idioma nativo, false si está aprendiéndolo
     * @return Una nueva instancia de UserLanguage
     * @throws IllegalArgumentException si los IDs son inválidos
     */
    public static UserLanguage create(Long userId, Long languageId, boolean isNative) {
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("El ID de usuario debe ser válido");
        }

        if (languageId == null || languageId <= 0) {
            throw new IllegalArgumentException("El ID de idioma debe ser válido");
        }

        return UserLanguage.builder()
                .userId(userId)
                .languageId(languageId)
                .isNative(isNative)
                .addedAt(LocalDateTime.now())
                .build();
    }

    /**
     * Factory method para crear un idioma nativo del usuario
     *
     * @param userId ID del usuario
     * @param languageId ID del idioma
     * @return Una nueva instancia de UserLanguage marcada como nativa
     */
    public static UserLanguage createNative(Long userId, Long languageId) {
        return create(userId, languageId, true);
    }

    /**
     * Factory method para crear un idioma que el usuario está aprendiendo
     *
     * @param userId ID del usuario
     * @param languageId ID del idioma
     * @return Una nueva instancia de UserLanguage marcada como idioma de aprendizaje
     */
    public static UserLanguage createLearning(Long userId, Long languageId) {
        return create(userId, languageId, false);
    }

    /**
     * Crea una nueva instancia de UserLanguage con un ID asignado
     * Útil cuando se persiste y se necesita asignar el ID generado
     *
     * @param id El ID de la relación usuario-idioma
     * @return Una nueva instancia con el ID asignado
     */
    public UserLanguage withId(Long id) {
        return UserLanguage.builder()
                .id(id)
                .userId(this.userId)
                .languageId(this.languageId)
                .isNative(this.isNative)
                .addedAt(this.addedAt)
                .build();
    }

    /**
     * Valida que la relación usuario-idioma sea válida
     * @return true si userId y languageId son válidos
     */
    public boolean isValid() {
        return userId != null && userId > 0
            && languageId != null && languageId > 0;
    }
}
