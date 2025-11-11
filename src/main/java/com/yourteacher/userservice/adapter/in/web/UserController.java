package com.yourteacher.userservice.adapter.in.web;

import com.yourteacher.userservice.adapter.in.web.dto.UserLanguageResponse;
import com.yourteacher.userservice.adapter.in.web.dto.UserRequest;
import com.yourteacher.userservice.adapter.in.web.dto.UserResponse;
import com.yourteacher.userservice.adapter.in.web.mapper.UserDtoMapper;
import com.yourteacher.userservice.adapter.in.web.mapper.UserLanguageDtoMapper;
import com.yourteacher.userservice.domain.model.Language;
import com.yourteacher.userservice.domain.model.User;
import com.yourteacher.userservice.domain.model.UserLanguage;
import com.yourteacher.userservice.domain.port.in.GetUserProfileUseCase;
import com.yourteacher.userservice.domain.port.in.UserService;
import com.yourteacher.userservice.domain.port.out.LanguageRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Controlador REST para operaciones de usuarios
 * Adapter Layer - Primary Adapter (Input)
 */
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final GetUserProfileUseCase getUserProfileUseCase;
    private final UserDtoMapper mapper;
    private final UserLanguageDtoMapper userLanguageMapper;
    private final LanguageRepository languageRepository;
    
    /**
     * POST /api/v1/users - Registrar nuevo usuario
     */
    @PostMapping
    public ResponseEntity<UserResponse> registerUser(@Valid @RequestBody UserRequest request) {
        User user = mapper.toDomain(request);
        User createdUser = userService.registerUser(user);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(mapper.toResponse(createdUser));
    }
    
    /**
     * GET /api/v1/users/{id} - Obtener usuario por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        return userService.getUserById(id)
                .map(user -> ResponseEntity.ok(mapper.toResponse(user)))
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * GET /api/v1/users/email/{email} - Obtener usuario por email
     */
    @GetMapping("/email/{email}")
    public ResponseEntity<UserResponse> getUserByEmail(@PathVariable String email) {
        return userService.getUserByEmail(email)
                .map(user -> ResponseEntity.ok(mapper.toResponse(user)))
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * GET /api/v1/users - Obtener todos los usuarios
     */
    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<UserResponse> users = userService.getAllUsers().stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(users);
    }
    
    /**
     * PUT /api/v1/users/{id} - Actualizar usuario
     */
    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserRequest request) {
        User user = mapper.toDomain(request);
        User updatedUser = userService.updateUser(id, user);
        return ResponseEntity.ok(mapper.toResponse(updatedUser));
    }
    
    /**
     * DELETE /api/v1/users/{id} - Eliminar usuario
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
    
    /**
     * PATCH /api/v1/users/{id}/activate - Activar usuario
     */
    @PatchMapping("/{id}/activate")
    public ResponseEntity<UserResponse> activateUser(@PathVariable Long id) {
        User activatedUser = userService.activateUser(id);
        return ResponseEntity.ok(mapper.toResponse(activatedUser));
    }
    
    /**
     * PATCH /api/v1/users/{id}/deactivate - Desactivar usuario
     */
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<UserResponse> deactivateUser(@PathVariable Long id) {
        User deactivatedUser = userService.deactivateUser(id);
        return ResponseEntity.ok(mapper.toResponse(deactivatedUser));
    }

    /**
     * GET /api/v1/users/{id}/profile - Obtener perfil completo del usuario con idiomas
     */
    @GetMapping("/{id}/profile")
    public ResponseEntity<UserResponse> getUserProfile(@PathVariable Long id) {
        // Obtener perfil completo (usuario + idiomas)
        GetUserProfileUseCase.UserProfile profile = getUserProfileUseCase.getUserProfile(id);

        // Obtener información detallada de los idiomas
        List<UserLanguage> userLanguages = profile.getLanguages();

        // Crear mapa de idiomas obteniendo cada uno por su ID
        Map<Long, Language> languagesMap = userLanguages.stream()
                .map(UserLanguage::getLanguageId)
                .distinct()
                .map(languageRepository::findById)
                .filter(java.util.Optional::isPresent)
                .map(java.util.Optional::get)
                .collect(Collectors.toMap(Language::getId, lang -> lang));

        // Mapear idiomas a DTOs
        List<UserLanguageResponse> languageResponses = userLanguageMapper.toResponseList(
                userLanguages,
                languagesMap
        );

        // Crear respuesta completa
        UserResponse response = mapper.toResponse(profile.getUser());
        response.setLanguages(languageResponses);

        return ResponseEntity.ok(response);
    }
}
