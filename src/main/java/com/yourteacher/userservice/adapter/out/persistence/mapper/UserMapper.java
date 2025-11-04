package com.yourteacher.userservice.adapter.out.persistence.mapper;

import com.yourteacher.userservice.adapter.out.persistence.entity.UserEntity;
import com.yourteacher.userservice.domain.model.User;
import org.springframework.stereotype.Component;

/**
 * Mapper para convertir entre entidad JPA y modelo de dominio
 * Separa la capa de persistencia del dominio
 */
@Component
public class UserMapper {
    
    /**
     * Convierte de entidad JPA a modelo de dominio
     */
    public User toDomain(UserEntity entity) {
        if (entity == null) {
            return null;
        }
        
        return User.builder()
                .id(entity.getId())
                .email(entity.getEmail())
                .password(entity.getPassword())
                .firstName(entity.getFirstName())
                .lastName(entity.getLastName())
                .roles(entity.getRoles())
                .status(entity.getStatus())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
    
    /**
     * Convierte de modelo de dominio a entidad JPA
     */
    public UserEntity toEntity(User domain) {
        if (domain == null) {
            return null;
        }
        
        return UserEntity.builder()
                .id(domain.getId())
                .email(domain.getEmail())
                .password(domain.getPassword())
                .firstName(domain.getFirstName())
                .lastName(domain.getLastName())
                .roles(domain.getRoles())
                .status(domain.getStatus())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .build();
    }
}
