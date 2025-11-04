package com.yourteacher.userservice.domain.port.in;

import com.yourteacher.userservice.domain.model.Language;

import java.util.List;
import java.util.Optional;

/**
 * Puerto de entrada (Input Port) para consulta del catálogo de idiomas
 * Define los casos de uso para obtener información sobre idiomas disponibles
 * (Hexagonal Architecture - Primary Port)
 *
 * Este caso de uso permite:
 * - Consultar el catálogo completo de idiomas
 * - Buscar idiomas por ID o código
 * - Obtener idiomas recomendados para empezar
 */
public interface GetLanguageCatalogUseCase {

    /**
     * Obtiene el catálogo completo de idiomas disponibles
     *
     * Retorna todos los idiomas soportados por la plataforma.
     * Los idiomas están ordenados alfabéticamente por nombre.
     *
     * @return Lista de todos los idiomas disponibles
     *         Lista vacía si no hay idiomas en el catálogo
     */
    List<Language> getAllLanguages();

    /**
     * Busca un idioma específico por su ID
     *
     * Útil cuando se tiene una referencia directa al idioma.
     *
     * @param id ID del idioma a buscar
     * @return Optional con el idioma si existe, Optional.empty() si no existe
     */
    Optional<Language> getLanguageById(Long id);

    /**
     * Busca un idioma específico por su código ISO 639-1
     *
     * El código debe ser de 2 letras en minúsculas (ej: "es", "en", "fr").
     * La búsqueda es case-insensitive.
     *
     * Ejemplos de códigos válidos:
     * - "es" -> Spanish
     * - "en" -> English
     * - "fr" -> French
     * - "de" -> German
     *
     * @param code Código ISO 639-1 del idioma (2 letras)
     * @return Optional con el idioma si existe, Optional.empty() si no existe
     */
    Optional<Language> getLanguageByCode(String code);

    /**
     * Obtiene una lista de idiomas recomendados para empezar
     *
     * Retorna los idiomas más populares y ampliamente hablados,
     * ideales para usuarios que están comenzando en la plataforma.
     *
     * La lista está ordenada por popularidad y número de hablantes.
     * Típicamente incluye idiomas como:
     * - English (en)
     * - Spanish (es)
     * - French (fr)
     * - German (de)
     * - Portuguese (pt)
     * - Italian (it)
     * - Japanese (ja)
     * - Korean (ko)
     * - Chinese (zh)
     * - Russian (ru)
     *
     * @return Lista de hasta 10 idiomas recomendados para empezar
     *         Lista vacía si no hay idiomas disponibles
     */
    List<Language> getStartingLanguages();

    /**
     * Busca idiomas cuyo nombre contenga el texto especificado
     *
     * La búsqueda es case-insensitive y busca coincidencias parciales.
     * Útil para funcionalidades de autocompletado o búsqueda.
     *
     * Ejemplos:
     * - "spa" -> Spanish
     * - "eng" -> English
     * - "man" -> German, Romanian
     *
     * @param searchTerm Texto a buscar en el nombre del idioma
     * @return Lista de idiomas que coinciden con la búsqueda
     *         Lista vacía si no hay coincidencias
     */
    List<Language> searchLanguagesByName(String searchTerm);

    /**
     * Verifica si un idioma existe en el catálogo
     *
     * Útil para validaciones rápidas sin necesidad de cargar el objeto completo.
     *
     * @param languageId ID del idioma a verificar
     * @return true si el idioma existe, false en caso contrario
     */
    boolean languageExists(Long languageId);

    /**
     * Verifica si un código de idioma existe en el catálogo
     *
     * Útil para validaciones rápidas sin necesidad de cargar el objeto completo.
     *
     * @param code Código ISO 639-1 del idioma
     * @return true si el código existe, false en caso contrario
     */
    boolean languageCodeExists(String code);
}
