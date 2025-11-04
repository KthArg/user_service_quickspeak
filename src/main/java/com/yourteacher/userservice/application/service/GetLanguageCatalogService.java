package com.yourteacher.userservice.application.service;

import com.yourteacher.userservice.domain.model.Language;
import com.yourteacher.userservice.domain.port.in.GetLanguageCatalogUseCase;
import com.yourteacher.userservice.domain.port.out.LanguageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Implementación del servicio de consulta del catálogo de idiomas (Application Layer)
 * Orquesta los casos de uso relacionados con consultar idiomas disponibles.
 *
 * Este servicio proporciona acceso de solo lectura al catálogo de idiomas,
 * permitiendo buscar idiomas por diversos criterios y obtener listas recomendadas.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetLanguageCatalogService implements GetLanguageCatalogUseCase {

    private final LanguageRepository languageRepository;

    /**
     * IDs de los idiomas más populares para usuarios nuevos
     * Top 10 idiomas más hablados y estudiados en el mundo
     */
    private static final List<Long> STARTING_LANGUAGE_IDS =
            List.of(1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L);

    /**
     * Obtiene el catálogo completo de idiomas disponibles
     *
     * @return Lista de todos los idiomas disponibles, ordenados alfabéticamente
     */
    @Override
    public List<Language> getAllLanguages() {
        return languageRepository.findAll();
    }

    /**
     * Busca un idioma específico por su ID
     *
     * @param id ID del idioma a buscar
     * @return Optional con el idioma si existe, Optional.empty() si no existe
     */
    @Override
    public Optional<Language> getLanguageById(Long id) {
        return languageRepository.findById(id);
    }

    /**
     * Busca un idioma específico por su código ISO 639-1
     *
     * La búsqueda es case-insensitive.
     *
     * @param code Código ISO 639-1 del idioma (2 letras, ej: "es", "en")
     * @return Optional con el idioma si existe, Optional.empty() si no existe
     */
    @Override
    public Optional<Language> getLanguageByCode(String code) {
        if (code == null || code.trim().isEmpty()) {
            return Optional.empty();
        }
        return languageRepository.findByCode(code.toLowerCase());
    }

    /**
     * Obtiene una lista de idiomas recomendados para usuarios nuevos
     *
     * Retorna los primeros 10 idiomas del catálogo, que corresponden
     * a los idiomas más populares y ampliamente hablados:
     * English, Spanish, French, German, Portuguese, Italian, Japanese, Korean, Chinese, Russian
     *
     * @return Lista de hasta 10 idiomas recomendados para empezar
     */
    @Override
    public List<Language> getStartingLanguages() {
        // Usar el método del repositorio si está implementado
        List<Language> startingLanguages = languageRepository.findStartingLanguages();

        // Si el repositorio no retorna idiomas, intentar obtenerlos por ID
        if (startingLanguages.isEmpty()) {
            startingLanguages = STARTING_LANGUAGE_IDS.stream()
                    .map(languageRepository::findById)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .toList();
        }

        return startingLanguages;
    }

    /**
     * Busca idiomas cuyo nombre contenga el texto especificado
     *
     * La búsqueda es case-insensitive y busca coincidencias parciales.
     *
     * @param searchTerm Texto a buscar en el nombre del idioma
     * @return Lista de idiomas que coinciden con la búsqueda
     */
    @Override
    public List<Language> searchLanguagesByName(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return List.of();
        }
        return languageRepository.searchByName(searchTerm.trim());
    }

    /**
     * Verifica si un idioma existe en el catálogo
     *
     * @param languageId ID del idioma a verificar
     * @return true si el idioma existe, false en caso contrario
     */
    @Override
    public boolean languageExists(Long languageId) {
        if (languageId == null) {
            return false;
        }
        return languageRepository.existsById(languageId);
    }

    /**
     * Verifica si un código de idioma existe en el catálogo
     *
     * La verificación es case-insensitive.
     *
     * @param code Código ISO 639-1 del idioma
     * @return true si el código existe, false en caso contrario
     */
    @Override
    public boolean languageCodeExists(String code) {
        if (code == null || code.trim().isEmpty()) {
            return false;
        }
        return languageRepository.existsByCode(code.toLowerCase());
    }
}
