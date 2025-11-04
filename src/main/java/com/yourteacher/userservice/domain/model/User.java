package com.yourteacher.userservice.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * Entidad de dominio User (Hexagonal Architecture - Domain Layer)
 * Representa un usuario del sistema con su lógica de negocio
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    
    private Long id;
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private String avatarSeed;
    private Set<Role> roles;
    private UserStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    /**
     * Valida que el email tenga un formato básico correcto
     */
    public boolean hasValidEmail() {
        return email != null && email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }
    
    /**
     * Verifica si el usuario está activo
     */
    public boolean isActive() {
        return UserStatus.ACTIVE.equals(status);
    }
    
    /**
     * Verifica si el usuario tiene un rol específico
     */
    public boolean hasRole(Role role) {
        return roles != null && roles.contains(role);
    }
    
    /**
     * Activa el usuario
     */
    public void activate() {
        this.status = UserStatus.ACTIVE;
    }
    
    /**
     * Desactiva el usuario
     */
    public void deactivate() {
        this.status = UserStatus.INACTIVE;
    }
    
    /**
     * Obtiene el nombre completo del usuario
     */
    public String getFullName() {
        return firstName + " " + lastName;
    }
}
