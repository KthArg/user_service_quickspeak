package com.yourteacher.userservice.domain.port.in;

import com.yourteacher.userservice.domain.exception.LanguageAlreadyAddedException;
import com.yourteacher.userservice.domain.exception.LanguageNotFoundException;
import com.yourteacher.userservice.domain.exception.MultipleNativeLanguagesException;
import com.yourteacher.userservice.domain.exception.UserNotFoundException;
import com.yourteacher.userservice.domain.model.UserLanguage;

import java.util.List;

/**
 * Puerto de entrada (Input Port) para gestión de idiomas de usuarios
 * Define los casos de uso para asignar y gestionar idiomas de usuarios
 * (Hexagonal Architecture - Primary Port)
 *
 * Este caso de uso permite:
 * - Agregar idiomas a usuarios
 * - Marcar idiomas como nativos
 * - Remover idiomas de usuarios
 * - Consultar idiomas de un usuario
 */
public interface ManageUserLanguagesUseCase {

    /**
     * Agrega un idioma a la lista de idiomas de un usuario
     *
     * El idioma se agrega como idioma de aprendizaje (no nativo) por defecto.
     * Si se desea marcarlo como nativo, usar setNativeLanguage().
     *
     * Precondiciones:
     * - El usuario debe existir en el sistema
     * - El idioma debe existir en el catálogo
     * - El usuario no debe tener ya ese idioma agregado
     *
     * @param userId ID del usuario al que se le agregará el idioma
     * @param languageId ID del idioma a agregar
     * @return UserLanguage creado con la relación usuario-idioma
     * @throws UserNotFoundException si el usuario no existe
     * @throws LanguageNotFoundException si el idioma no existe
     * @throws LanguageAlreadyAddedException si el usuario ya tiene ese idioma
     */
    UserLanguage addLanguageToUser(Long userId, Long languageId);

    /**
     * Marca un idioma como nativo para un usuario
     *
     * El idioma debe estar previamente agregado al usuario.
     * Un usuario solo puede tener UN idioma nativo.
     * Si el usuario ya tiene otro idioma marcado como nativo, se lanzará una excepción.
     *
     * Precondiciones:
     * - El usuario debe existir en el sistema
     * - El idioma debe existir en el catálogo
     * - El usuario debe tener el idioma agregado
     * - El usuario NO debe tener ya otro idioma marcado como nativo
     *
     * @param userId ID del usuario
     * @param languageId ID del idioma a marcar como nativo
     * @return UserLanguage actualizado con isNative=true
     * @throws UserNotFoundException si el usuario no existe
     * @throws LanguageNotFoundException si el usuario no tiene ese idioma agregado
     * @throws MultipleNativeLanguagesException si el usuario ya tiene otro idioma nativo
     */
    UserLanguage setNativeLanguage(Long userId, Long languageId);

    /**
     * Remueve un idioma de la lista de idiomas de un usuario
     *
     * Elimina la relación entre el usuario y el idioma.
     * Si el idioma no estaba agregado, la operación no tiene efecto.
     *
     * Precondiciones:
     * - El usuario debe existir en el sistema
     *
     * @param userId ID del usuario
     * @param languageId ID del idioma a remover
     * @throws UserNotFoundException si el usuario no existe
     */
    void removeLanguageFromUser(Long userId, Long languageId);

    /**
     * Obtiene todos los idiomas asociados a un usuario
     *
     * Retorna tanto idiomas nativos como idiomas que está aprendiendo.
     * La lista puede estar vacía si el usuario no tiene idiomas agregados.
     *
     * Precondiciones:
     * - El usuario debe existir en el sistema
     *
     * @param userId ID del usuario
     * @return Lista de UserLanguage con todos los idiomas del usuario
     *         Lista vacía si el usuario no tiene idiomas
     * @throws UserNotFoundException si el usuario no existe
     */
    List<UserLanguage> getUserLanguages(Long userId);

    /**
     * Obtiene el idioma nativo de un usuario
     *
     * Un usuario puede tener como máximo un idioma nativo.
     * Si el usuario no tiene idioma nativo configurado, retorna null.
     *
     * Precondiciones:
     * - El usuario debe existir en el sistema
     *
     * @param userId ID del usuario
     * @return UserLanguage marcado como nativo, o null si no tiene
     * @throws UserNotFoundException si el usuario no existe
     */
    UserLanguage getNativeLanguage(Long userId);

    /**
     * Obtiene solo los idiomas que el usuario está aprendiendo
     *
     * Retorna idiomas con isNative=false.
     * Excluye el idioma nativo si existe.
     *
     * Precondiciones:
     * - El usuario debe existir en el sistema
     *
     * @param userId ID del usuario
     * @return Lista de UserLanguage con isNative=false
     *         Lista vacía si el usuario no está aprendiendo ningún idioma
     * @throws UserNotFoundException si el usuario no existe
     */
    List<UserLanguage> getLearningLanguages(Long userId);
}
