package com.yourteacher.userservice.adapter.out.persistence;

import com.yourteacher.userservice.adapter.out.persistence.entity.LanguageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio JPA para operaciones de base de datos con idiomas
 * Spring Data JPA generará la implementación automáticamente
 */
@Repository
public interface JpaLanguageRepository extends JpaRepository<LanguageEntity, Long> {

    /**
     * Busca un idioma por su código ISO 639-1 (case-insensitive)
     */
    @Query("SELECT l FROM LanguageEntity l WHERE LOWER(l.code) = LOWER(:code)")
    Optional<LanguageEntity> findByCodeIgnoreCase(@Param("code") String code);

    /**
     * Busca un idioma por su nombre exacto (case-insensitive)
     */
    @Query("SELECT l FROM LanguageEntity l WHERE LOWER(l.name) = LOWER(:name)")
    Optional<LanguageEntity> findByNameIgnoreCase(@Param("name") String name);

    /**
     * Busca idiomas cuyo nombre contenga el término de búsqueda (case-insensitive)
     */
    @Query("SELECT l FROM LanguageEntity l WHERE LOWER(l.name) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<LanguageEntity> searchByName(@Param("searchTerm") String searchTerm);

    /**
     * Verifica si existe un idioma con el código dado (case-insensitive)
     */
    @Query("SELECT CASE WHEN COUNT(l) > 0 THEN true ELSE false END FROM LanguageEntity l WHERE LOWER(l.code) = LOWER(:code)")
    boolean existsByCodeIgnoreCase(@Param("code") String code);

    /**
     * Obtiene los primeros 10 idiomas ordenados por nombre
     * (Esto simula los idiomas más populares - idealmente tendrías un campo popularity)
     */
    @Query("SELECT l FROM LanguageEntity l ORDER BY l.name ASC")
    List<LanguageEntity> findTop10ByOrderByNameAsc();
}
