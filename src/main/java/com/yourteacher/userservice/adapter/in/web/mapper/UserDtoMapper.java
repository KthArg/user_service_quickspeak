package com.yourteacher.userservice.adapter.in.web.mapper;

import com.yourteacher.userservice.adapter.in.web.dto.UserRequest;
import com.yourteacher.userservice.adapter.in.web.dto.UserResponse;
import com.yourteacher.userservice.domain.model.Role;
import com.yourteacher.userservice.domain.model.User;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * Mapper para convertir entre DTOs y modelo de dominio
 */
@Component
public class UserDtoMapper {
    
    /**
     * Convierte UserRequest a User del dominio
     */
    public User toDomain(UserRequest request) {
        if (request == null) {
            return null;
        }
        
        // Si no se especifican roles, asignar STUDENT por defecto
        Set<Role> roles = request.getRoles();
        if (roles == null || roles.isEmpty()) {
            roles = Set.of(Role.STUDENT);
        }
        
        return User.builder()
                .email(request.getEmail())
                .password(request.getPassword())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .roles(roles)
                .build();
    }
    
    /**
     * Convierte User del dominio a UserResponse
     */
    public UserResponse toResponse(User user) {
        if (user == null) {
            return null;
        }
        
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .fullName(user.getFullName())
                .roles(user.getRoles())
                .status(user.getStatus())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
