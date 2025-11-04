package com.yourteacher.userservice.adapter.in.web.dto;

import com.yourteacher.userservice.domain.model.Role;
import com.yourteacher.userservice.domain.model.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * DTO para respuestas con información de usuarios
 * No incluye la contraseña por seguridad
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private String fullName;
    private Set<Role> roles;
    private UserStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
