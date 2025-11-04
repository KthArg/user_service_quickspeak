package com.yourteacher.userservice.adapter.in.web;

import com.yourteacher.userservice.adapter.in.web.dto.LanguageResponse;
import com.yourteacher.userservice.adapter.in.web.mapper.LanguageDtoMapper;
import com.yourteacher.userservice.domain.port.in.GetLanguageCatalogUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para operaciones de catálogo de idiomas
 * Adapter Layer - Primary Adapter (Input)
 */
@RestController
@RequestMapping("/api/v1/languages")
@RequiredArgsConstructor
public class LanguageController {

    private final GetLanguageCatalogUseCase getLanguageCatalog;
    private final LanguageDtoMapper mapper;

    /**
     * GET /api/v1/languages - Obtener todos los idiomas disponibles
     */
    @GetMapping
    public ResponseEntity<List<LanguageResponse>> getAllLanguages() {
        var languages = getLanguageCatalog.getAllLanguages();
        return ResponseEntity.ok(mapper.toResponseList(languages));
    }

    /**
     * GET /api/v1/languages/{id} - Obtener idioma por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<LanguageResponse> getLanguageById(@PathVariable Long id) {
        return getLanguageCatalog.getLanguageById(id)
                .map(language -> ResponseEntity.ok(mapper.toResponse(language)))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * GET /api/v1/languages/code/{code} - Obtener idioma por código ISO
     */
    @GetMapping("/code/{code}")
    public ResponseEntity<LanguageResponse> getLanguageByCode(@PathVariable String code) {
        return getLanguageCatalog.getLanguageByCode(code)
                .map(language -> ResponseEntity.ok(mapper.toResponse(language)))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * GET /api/v1/languages/starting - Obtener idiomas recomendados para empezar
     */
    @GetMapping("/starting")
    public ResponseEntity<List<LanguageResponse>> getStartingLanguages() {
        var languages = getLanguageCatalog.getStartingLanguages();
        return ResponseEntity.ok(mapper.toResponseList(languages));
    }

    /**
     * GET /api/v1/languages/search?q={query} - Buscar idiomas por nombre
     */
    @GetMapping("/search")
    public ResponseEntity<List<LanguageResponse>> searchLanguages(@RequestParam("q") String query) {
        var languages = getLanguageCatalog.searchLanguagesByName(query);
        return ResponseEntity.ok(mapper.toResponseList(languages));
    }
}
