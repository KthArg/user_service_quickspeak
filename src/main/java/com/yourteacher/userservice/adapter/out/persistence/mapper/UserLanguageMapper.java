package com.yourteacher.userservice.adapter.out.persistence.mapper;

import com.yourteacher.userservice.adapter.out.persistence.entity.UserLanguageEntity;
import com.yourteacher.userservice.domain.model.UserLanguage;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper para convertir entre entidad JPA y modelo de dominio de UserLanguage
 * Separa la capa de persistencia del dominio
 */
@Component
public class UserLanguageMapper {

    /**
     * Convierte de entidad JPA a modelo de dominio
     */
    public UserLanguage toDomain(UserLanguageEntity entity) {
        if (entity == null) {
            return null;
        }

        return UserLanguage.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .languageId(entity.getLanguageId())
                .isNative(entity.isNative())
                .addedAt(entity.getAddedAt())
                .build();
    }

    /**
     * Convierte de modelo de dominio a entidad JPA
     */
    public UserLanguageEntity toEntity(UserLanguage domain) {
        if (domain == null) {
            return null;
        }

        return UserLanguageEntity.builder()
                .id(domain.getId())
                .userId(domain.getUserId())
                .languageId(domain.getLanguageId())
                .isNative(domain.isNative())
                .addedAt(domain.getAddedAt())
                .build();
    }

    /**
     * Convierte una lista de entidades JPA a modelos de dominio
     */
    public List<UserLanguage> toDomainList(List<UserLanguageEntity> entities) {
        if (entities == null) {
            return null;
        }

        return entities.stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    /**
     * Convierte una lista de modelos de dominio a entidades JPA
     */
    public List<UserLanguageEntity> toEntityList(List<UserLanguage> domains) {
        if (domains == null) {
            return null;
        }

        return domains.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }
}
