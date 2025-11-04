package com.yourteacher.userservice.adapter.in.web;

import com.yourteacher.userservice.adapter.in.web.dto.AddLanguageRequest;
import com.yourteacher.userservice.adapter.in.web.dto.UserLanguageResponse;
import com.yourteacher.userservice.adapter.in.web.mapper.UserLanguageDtoMapper;
import com.yourteacher.userservice.domain.model.Language;
import com.yourteacher.userservice.domain.port.in.GetLanguageCatalogUseCase;
import com.yourteacher.userservice.domain.port.in.ManageUserLanguagesUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Controlador REST para gestión de idiomas de usuarios
 * Adapter Layer - Primary Adapter (Input)
 */
@RestController
@RequestMapping("/api/v1/users/{userId}/languages")
@RequiredArgsConstructor
public class UserLanguageController {

    private final ManageUserLanguagesUseCase manageUserLanguages;
    private final GetLanguageCatalogUseCase getLanguageCatalog;
    private final UserLanguageDtoMapper mapper;

    /**
     * GET /api/v1/users/{userId}/languages - Obtener todos los idiomas del usuario
     */
    @GetMapping
    public ResponseEntity<List<UserLanguageResponse>> getUserLanguages(@PathVariable Long userId) {
        var userLanguages = manageUserLanguages.getUserLanguages(userId);

        // Obtener información de los idiomas
        var languageIds = userLanguages.stream()
                .map(ul -> ul.getLanguageId())
                .collect(Collectors.toSet());

        Map<Long, Language> languagesMap = languageIds.stream()
                .map(id -> getLanguageCatalog.getLanguageById(id))
                .filter(opt -> opt.isPresent())
                .map(opt -> opt.get())
                .collect(Collectors.toMap(Language::getId, lang -> lang));

        var responses = mapper.toResponseList(userLanguages, languagesMap);
        return ResponseEntity.ok(responses);
    }

    /**
     * GET /api/v1/users/{userId}/languages/native - Obtener idioma nativo del usuario
     */
    @GetMapping("/native")
    public ResponseEntity<UserLanguageResponse> getNativeLanguage(@PathVariable Long userId) {
        var nativeLanguage = manageUserLanguages.getNativeLanguage(userId);

        if (nativeLanguage == null) {
            return ResponseEntity.notFound().build();
        }

        var language = getLanguageCatalog.getLanguageById(nativeLanguage.getLanguageId());

        if (language.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(mapper.toResponse(nativeLanguage, language.get()));
    }

    /**
     * GET /api/v1/users/{userId}/languages/learning - Obtener idiomas de aprendizaje
     */
    @GetMapping("/learning")
    public ResponseEntity<List<UserLanguageResponse>> getLearningLanguages(@PathVariable Long userId) {
        var learningLanguages = manageUserLanguages.getLearningLanguages(userId);

        // Obtener información de los idiomas
        var languageIds = learningLanguages.stream()
                .map(ul -> ul.getLanguageId())
                .collect(Collectors.toSet());

        Map<Long, Language> languagesMap = languageIds.stream()
                .map(id -> getLanguageCatalog.getLanguageById(id))
                .filter(opt -> opt.isPresent())
                .map(opt -> opt.get())
                .collect(Collectors.toMap(Language::getId, lang -> lang));

        var responses = mapper.toResponseList(learningLanguages, languagesMap);
        return ResponseEntity.ok(responses);
    }

    /**
     * POST /api/v1/users/{userId}/languages - Agregar idioma a usuario
     */
    @PostMapping
    public ResponseEntity<Void> addLanguage(
            @PathVariable Long userId,
            @Valid @RequestBody AddLanguageRequest request) {
        manageUserLanguages.addLanguageToUser(userId, request.getLanguageId());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * PATCH /api/v1/users/{userId}/languages/{languageId}/native - Marcar idioma como nativo
     */
    @PatchMapping("/{languageId}/native")
    public ResponseEntity<Void> setAsNative(
            @PathVariable Long userId,
            @PathVariable Long languageId) {
        manageUserLanguages.setNativeLanguage(userId, languageId);
        return ResponseEntity.noContent().build();
    }

    /**
     * DELETE /api/v1/users/{userId}/languages/{languageId} - Remover idioma de usuario
     */
    @DeleteMapping("/{languageId}")
    public ResponseEntity<Void> removeLanguage(
            @PathVariable Long userId,
            @PathVariable Long languageId) {
        manageUserLanguages.removeLanguageFromUser(userId, languageId);
        return ResponseEntity.noContent().build();
    }
}
