package com.yourteacher.userservice.application.service;

import com.yourteacher.userservice.domain.exception.LanguageAlreadyAddedException;
import com.yourteacher.userservice.domain.exception.LanguageNotAddedToUserException;
import com.yourteacher.userservice.domain.exception.LanguageNotFoundException;
import com.yourteacher.userservice.domain.exception.NativeLanguageCannotBeRemovedException;
import com.yourteacher.userservice.domain.exception.UserNotFoundException;
import com.yourteacher.userservice.domain.model.Language;
import com.yourteacher.userservice.domain.model.User;
import com.yourteacher.userservice.domain.model.UserLanguage;
import com.yourteacher.userservice.domain.port.in.ManageUserLanguagesUseCase;
import com.yourteacher.userservice.domain.port.out.LanguageRepository;
import com.yourteacher.userservice.domain.port.out.UserLanguageRepository;
import com.yourteacher.userservice.domain.port.out.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Implementación del servicio de gestión de idiomas de usuarios (Application Layer)
 * Orquesta los casos de uso relacionados con asignar, gestionar y consultar
 * idiomas de usuarios.
 *
 * Reglas de negocio implementadas:
 * - Un usuario puede tener MÚLTIPLES idiomas de aprendizaje
 * - Un usuario puede tener SOLO UN idioma nativo a la vez
 * - No se puede eliminar el idioma nativo (debe cambiarlo primero)
 * - No se puede agregar un idioma que ya tiene
 * - Para setNativeLanguage, el idioma debe estar ya agregado al usuario
 */
@Service
@RequiredArgsConstructor
@Transactional
public class ManageUserLanguagesService implements ManageUserLanguagesUseCase {

    private final UserLanguageRepository userLanguageRepository;
    private final LanguageRepository languageRepository;
    private final UserRepository userRepository;

    /**
     * Agrega un idioma a la lista de idiomas de aprendizaje de un usuario
     *
     * @param userId ID del usuario al que se le agregará el idioma
     * @param languageId ID del idioma a agregar
     * @return UserLanguage creado con la relación usuario-idioma
     * @throws UserNotFoundException si el usuario no existe
     * @throws LanguageNotFoundException si el idioma no existe
     * @throws LanguageAlreadyAddedException si el usuario ya tiene ese idioma
     */
    @Override
    public UserLanguage addLanguageToUser(Long userId, Long languageId) {
        // 1. Validar que el usuario existe
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        // 2. Validar que el idioma existe en el catálogo
        Language language = languageRepository.findById(languageId)
                .orElseThrow(() -> new LanguageNotFoundException(languageId));

        // 3. Validar que el usuario no tenga ya ese idioma
        if (userLanguageRepository.existsByUserIdAndLanguageId(userId, languageId)) {
            throw new LanguageAlreadyAddedException(userId, languageId);
        }

        // 4. Crear y guardar UserLanguage (como idioma de aprendizaje, no nativo)
        UserLanguage userLanguage = UserLanguage.create(userId, languageId, false);
        return userLanguageRepository.save(userLanguage);
    }

    /**
     * Marca un idioma como nativo para un usuario
     *
     * Desmarca automáticamente el idioma nativo anterior si existe.
     * El idioma debe estar previamente agregado al usuario.
     *
     * @param userId ID del usuario
     * @param languageId ID del idioma a marcar como nativo
     * @return UserLanguage actualizado con isNative=true
     * @throws UserNotFoundException si el usuario no existe
     * @throws LanguageNotFoundException si el idioma no existe en el catálogo
     * @throws LanguageNotAddedToUserException si el usuario no tiene ese idioma agregado
     */
    @Override
    public UserLanguage setNativeLanguage(Long userId, Long languageId) {
        // 1. Validar que el usuario existe
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        // 2. Validar que el idioma existe en el catálogo
        Language language = languageRepository.findById(languageId)
                .orElseThrow(() -> new LanguageNotFoundException(languageId));

        // 3. Verificar que el usuario ya tenga ese idioma agregado
        UserLanguage userLanguage = userLanguageRepository.findByUserIdAndLanguageId(userId, languageId)
                .orElseThrow(() -> new LanguageNotAddedToUserException(userId, languageId));

        // 4. Desmarcar el idioma nativo anterior (si existe)
        Optional<UserLanguage> currentNative = userLanguageRepository.findNativeLanguageByUserId(userId);
        if (currentNative.isPresent() && !currentNative.get().getLanguageId().equals(languageId)) {
            UserLanguage oldNative = currentNative.get();
            UserLanguage updatedOldNative = UserLanguage.builder()
                    .id(oldNative.getId())
                    .userId(oldNative.getUserId())
                    .languageId(oldNative.getLanguageId())
                    .isNative(false)
                    .addedAt(oldNative.getAddedAt())
                    .build();
            userLanguageRepository.save(updatedOldNative);
        }

        // 5. Marcar el nuevo como nativo
        UserLanguage updatedUserLanguage = UserLanguage.builder()
                .id(userLanguage.getId())
                .userId(userLanguage.getUserId())
                .languageId(userLanguage.getLanguageId())
                .isNative(true)
                .addedAt(userLanguage.getAddedAt())
                .build();

        return userLanguageRepository.save(updatedUserLanguage);
    }

    /**
     * Remueve un idioma de la lista de idiomas de un usuario
     *
     * No permite eliminar el idioma nativo. El usuario debe cambiar
     * su idioma nativo antes de poder eliminarlo.
     *
     * @param userId ID del usuario
     * @param languageId ID del idioma a remover
     * @throws UserNotFoundException si el usuario no existe
     * @throws NativeLanguageCannotBeRemovedException si se intenta eliminar el idioma nativo
     */
    @Override
    public void removeLanguageFromUser(Long userId, Long languageId) {
        // 1. Validar que el usuario existe
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        // 2. Verificar si el idioma está agregado al usuario
        Optional<UserLanguage> userLanguage = userLanguageRepository
                .findByUserIdAndLanguageId(userId, languageId);

        // Si no está agregado, no hacer nada (operación idempotente)
        if (userLanguage.isEmpty()) {
            return;
        }

        // 3. No permitir eliminar el idioma nativo
        if (userLanguage.get().isNative()) {
            throw new NativeLanguageCannotBeRemovedException(userId, languageId);
        }

        // 4. Eliminar
        userLanguageRepository.deleteByUserIdAndLanguageId(userId, languageId);
    }

    /**
     * Obtiene todos los idiomas asociados a un usuario
     *
     * Incluye tanto el idioma nativo como los idiomas de aprendizaje.
     *
     * @param userId ID del usuario
     * @return Lista de UserLanguage con todos los idiomas del usuario
     * @throws UserNotFoundException si el usuario no existe
     */
    @Override
    @Transactional(readOnly = true)
    public List<UserLanguage> getUserLanguages(Long userId) {
        // Validar que el usuario existe
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        return userLanguageRepository.findByUserId(userId);
    }

    /**
     * Obtiene el idioma nativo de un usuario
     *
     * @param userId ID del usuario
     * @return UserLanguage marcado como nativo, o null si no tiene
     * @throws UserNotFoundException si el usuario no existe
     */
    @Override
    @Transactional(readOnly = true)
    public UserLanguage getNativeLanguage(Long userId) {
        // Validar que el usuario existe
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        return userLanguageRepository.findNativeLanguageByUserId(userId)
                .orElse(null);
    }

    /**
     * Obtiene solo los idiomas que el usuario está aprendiendo
     *
     * Excluye el idioma nativo (isNative=false).
     *
     * @param userId ID del usuario
     * @return Lista de UserLanguage con isNative=false
     * @throws UserNotFoundException si el usuario no existe
     */
    @Override
    @Transactional(readOnly = true)
    public List<UserLanguage> getLearningLanguages(Long userId) {
        // Validar que el usuario existe
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        return userLanguageRepository.findLearningLanguagesByUserId(userId);
    }
}
