package com.yourteacher.userservice.domain.port.out;

import com.yourteacher.userservice.domain.model.Language;

import java.util.List;
import java.util.Optional;

/**
 * Puerto de salida (Output Port) para persistencia de idiomas
 * Define las operaciones que el dominio necesita para persistir idiomas
 * Hexagonal Architecture - Secondary Port
 */
public interface LanguageRepository {

    /**
     * Guarda un idioma en el repositorio
     * Si el idioma ya existe (tiene ID), lo actualiza
     *
     * @param language Idioma a guardar
     * @return Idioma guardado con ID asignado
     */
    Language save(Language language);

    /**
     * Obtiene todos los idiomas disponibles
     * Los idiomas deben estar ordenados alfabéticamente por nombre
     *
     * @return Lista de todos los idiomas
     */
    List<Language> findAll();

    /**
     * Busca un idioma por su ID
     *
     * @param id ID del idioma a buscar
     * @return Optional con el idioma si existe, Optional.empty() si no existe
     */
    Optional<Language> findById(Long id);

    /**
     * Busca un idioma por su código ISO 639-1
     * La búsqueda debe ser case-insensitive
     *
     * @param code Código ISO 639-1 del idioma (ej: "es", "en")
     * @return Optional con el idioma si existe, Optional.empty() si no existe
     */
    Optional<Language> findByCode(String code);

    /**
     * Busca un idioma por su nombre exacto
     * La búsqueda debe ser case-insensitive
     *
     * @param name Nombre del idioma (ej: "Spanish", "English")
     * @return Optional con el idioma si existe, Optional.empty() si no existe
     */
    Optional<Language> findByName(String name);

    /**
     * Busca idiomas cuyo nombre contenga el término de búsqueda
     * La búsqueda debe ser case-insensitive y buscar coincidencias parciales
     *
     * @param searchTerm Término a buscar en el nombre del idioma
     * @return Lista de idiomas que coinciden con la búsqueda
     */
    List<Language> searchByName(String searchTerm);

    /**
     * Obtiene los idiomas más populares recomendados para empezar
     * Retorna hasta los primeros 10 idiomas más populares
     *
     * @return Lista de idiomas recomendados para usuarios nuevos
     */
    List<Language> findStartingLanguages();

    /**
     * Verifica si existe un idioma con el código dado
     *
     * @param code Código ISO 639-1 del idioma
     * @return true si existe, false en caso contrario
     */
    boolean existsByCode(String code);

    /**
     * Verifica si existe un idioma con el ID dado
     *
     * @param id ID del idioma
     * @return true si existe, false en caso contrario
     */
    boolean existsById(Long id);

    /**
     * Elimina un idioma por su ID
     *
     * @param id ID del idioma a eliminar
     */
    void deleteById(Long id);

    /**
     * Obtiene el total de idiomas disponibles en el catálogo
     *
     * @return Número total de idiomas
     */
    long count();
}
