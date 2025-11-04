package com.yourteacher.userservice.adapter.out.persistence;

import com.yourteacher.userservice.adapter.out.persistence.entity.UserLanguageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio JPA para operaciones de base de datos con relaciones usuario-idioma
 * Spring Data JPA generará la implementación automáticamente
 */
@Repository
public interface JpaUserLanguageRepository extends JpaRepository<UserLanguageEntity, Long> {

    /**
     * Obtiene todos los idiomas de un usuario
     */
    List<UserLanguageEntity> findByUserId(Long userId);

    /**
     * Busca una relación específica entre usuario e idioma
     */
    Optional<UserLanguageEntity> findByUserIdAndLanguageId(Long userId, Long languageId);

    /**
     * Obtiene el idioma nativo de un usuario
     */
    Optional<UserLanguageEntity> findByUserIdAndIsNativeTrue(Long userId);

    /**
     * Obtiene solo los idiomas de aprendizaje de un usuario (no nativos)
     */
    List<UserLanguageEntity> findByUserIdAndIsNativeFalse(Long userId);

    /**
     * Obtiene todos los usuarios que tienen un idioma específico
     */
    List<UserLanguageEntity> findByLanguageId(Long languageId);

    /**
     * Elimina una relación específica entre usuario e idioma
     */
    void deleteByUserIdAndLanguageId(Long userId, Long languageId);

    /**
     * Elimina todos los idiomas de un usuario
     */
    void deleteByUserId(Long userId);

    /**
     * Verifica si un usuario tiene un idioma específico
     */
    boolean existsByUserIdAndLanguageId(Long userId, Long languageId);

    /**
     * Verifica si un usuario tiene un idioma nativo
     */
    @Query("SELECT CASE WHEN COUNT(ul) > 0 THEN true ELSE false END FROM UserLanguageEntity ul WHERE ul.userId = :userId AND ul.isNative = true")
    boolean hasNativeLanguage(@Param("userId") Long userId);

    /**
     * Cuenta cuántos idiomas tiene un usuario
     */
    long countByUserId(Long userId);

    /**
     * Cuenta cuántos usuarios tienen un idioma específico
     */
    long countByLanguageId(Long languageId);
}
