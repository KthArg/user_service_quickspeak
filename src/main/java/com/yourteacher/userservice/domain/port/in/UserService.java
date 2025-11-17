package com.yourteacher.userservice.domain.port.in;

import com.yourteacher.userservice.domain.model.User;

import java.util.List;
import java.util.Optional;

/**
 * Puerto de entrada (Input Port) para casos de uso de usuarios
 * Define las operaciones disponibles para el dominio de usuarios
 * Hexagonal Architecture - Primary Port
 */
public interface UserService {
    
    /**
     * Registra un nuevo usuario en el sistema
     */
    User registerUser(User user);
    
    /**
     * Obtiene un usuario por su ID
     */
    Optional<User> getUserById(Long id);
    
    /**
     * Obtiene un usuario por su email
     */
    Optional<User> getUserByEmail(String email);
    
    /**
     * Obtiene todos los usuarios del sistema
     */
    List<User> getAllUsers();
    
    /**
     * Actualiza la información de un usuario
     */
    User updateUser(Long id, User user);
    
    /**
     * Elimina un usuario del sistema
     */
    void deleteUser(Long id);
    
    /**
     * Activa un usuario
     */
    User activateUser(Long id);
    
    /**
     * Desactiva un usuario
     */
    User deactivateUser(Long id);
    
    /**
     * Valida las credenciales de un usuario
     */
    boolean validateCredentials(String email, String password);

    /**
     * Cambia la contraseña de un usuario
     */
    User changePassword(Long id, String currentPassword, String newPassword);

    /**
     * Cambia el email de un usuario
     */
    User changeEmail(Long id, String newEmail);
}
