package com.yourteacher.userservice.domain.port.out;

import com.yourteacher.userservice.domain.model.UserLanguage;

import java.util.List;
import java.util.Optional;

/**
 * Puerto de salida (Output Port) para persistencia de relaciones usuario-idioma
 * Define las operaciones que el dominio necesita para gestionar idiomas de usuarios
 * Hexagonal Architecture - Secondary Port
 */
public interface UserLanguageRepository {

    /**
     * Guarda una relación usuario-idioma en el repositorio
     * Si la relación ya existe (tiene ID), la actualiza
     *
     * @param userLanguage Relación usuario-idioma a guardar
     * @return UserLanguage guardado con ID asignado
     */
    UserLanguage save(UserLanguage userLanguage);

    /**
     * Busca una relación usuario-idioma por su ID
     *
     * @param id ID de la relación a buscar
     * @return Optional con la relación si existe, Optional.empty() si no existe
     */
    Optional<UserLanguage> findById(Long id);

    /**
     * Obtiene todos los idiomas de un usuario (nativos y de aprendizaje)
     *
     * @param userId ID del usuario
     * @return Lista de todos los idiomas del usuario
     */
    List<UserLanguage> findByUserId(Long userId);

    /**
     * Busca una relación específica entre un usuario y un idioma
     * Útil para verificar si un usuario ya tiene un idioma agregado
     *
     * @param userId ID del usuario
     * @param languageId ID del idioma
     * @return Optional con la relación si existe, Optional.empty() si no existe
     */
    Optional<UserLanguage> findByUserIdAndLanguageId(Long userId, Long languageId);

    /**
     * Obtiene el idioma nativo de un usuario
     * Un usuario solo puede tener un idioma nativo
     *
     * @param userId ID del usuario
     * @return Optional con el idioma nativo si existe, Optional.empty() si no tiene idioma nativo
     */
    Optional<UserLanguage> findNativeLanguageByUserId(Long userId);

    /**
     * Obtiene solo los idiomas que el usuario está aprendiendo
     * Excluye el idioma nativo (isNative=false)
     *
     * @param userId ID del usuario
     * @return Lista de idiomas que el usuario está aprendiendo
     */
    List<UserLanguage> findLearningLanguagesByUserId(Long userId);

    /**
     * Obtiene todos los usuarios que están aprendiendo un idioma específico
     * Útil para estadísticas y análisis
     *
     * @param languageId ID del idioma
     * @return Lista de relaciones usuario-idioma para ese idioma
     */
    List<UserLanguage> findByLanguageId(Long languageId);

    /**
     * Verifica si un usuario ya tiene un idioma agregado
     *
     * @param userId ID del usuario
     * @param languageId ID del idioma
     * @return true si el usuario tiene ese idioma, false en caso contrario
     */
    boolean existsByUserIdAndLanguageId(Long userId, Long languageId);

    /**
     * Verifica si un usuario tiene un idioma nativo configurado
     *
     * @param userId ID del usuario
     * @return true si el usuario tiene un idioma nativo, false en caso contrario
     */
    boolean hasNativeLanguage(Long userId);

    /**
     * Elimina una relación usuario-idioma por su ID
     *
     * @param id ID de la relación a eliminar
     */
    void deleteById(Long id);

    /**
     * Elimina la relación específica entre un usuario y un idioma
     *
     * @param userId ID del usuario
     * @param languageId ID del idioma
     */
    void deleteByUserIdAndLanguageId(Long userId, Long languageId);

    /**
     * Elimina todos los idiomas de un usuario
     * Útil cuando se elimina un usuario del sistema
     *
     * @param userId ID del usuario
     */
    void deleteAllByUserId(Long userId);

    /**
     * Cuenta cuántos idiomas tiene un usuario
     *
     * @param userId ID del usuario
     * @return Número de idiomas del usuario
     */
    long countByUserId(Long userId);

    /**
     * Cuenta cuántos usuarios están aprendiendo un idioma específico
     *
     * @param languageId ID del idioma
     * @return Número de usuarios aprendiendo ese idioma
     */
    long countByLanguageId(Long languageId);
}
