package com.yourteacher.userservice.domain.port.out;

import com.yourteacher.userservice.domain.model.User;

import java.util.List;
import java.util.Optional;

/**
 * Puerto de salida (Output Port) para persistencia de usuarios
 * Define las operaciones que el dominio necesita para persistir datos
 * Hexagonal Architecture - Secondary Port
 */
public interface UserRepository {
    
    /**
     * Guarda un usuario en el repositorio
     */
    User save(User user);
    
    /**
     * Busca un usuario por su ID
     */
    Optional<User> findById(Long id);
    
    /**
     * Busca un usuario por su email
     */
    Optional<User> findByEmail(String email);
    
    /**
     * Obtiene todos los usuarios
     */
    List<User> findAll();
    
    /**
     * Verifica si existe un usuario con el email dado
     */
    boolean existsByEmail(String email);
    
    /**
     * Elimina un usuario por su ID
     */
    void deleteById(Long id);
    
    /**
     * Obtiene usuarios activos
     */
    List<User> findActiveUsers();
}
