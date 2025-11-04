package com.yourteacher.userservice.adapter.out.persistence.mapper;

import com.yourteacher.userservice.adapter.out.persistence.entity.LanguageEntity;
import com.yourteacher.userservice.domain.model.Language;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper para convertir entre entidad JPA y modelo de dominio de Language
 * Separa la capa de persistencia del dominio
 */
@Component
public class LanguageMapper {

    /**
     * Convierte de entidad JPA a modelo de dominio
     */
    public Language toDomain(LanguageEntity entity) {
        if (entity == null) {
            return null;
        }

        return Language.builder()
                .id(entity.getId())
                .name(entity.getName())
                .code(entity.getCode())
                .flagUrl(entity.getFlagUrl())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    /**
     * Convierte de modelo de dominio a entidad JPA
     */
    public LanguageEntity toEntity(Language domain) {
        if (domain == null) {
            return null;
        }

        return LanguageEntity.builder()
                .id(domain.getId())
                .name(domain.getName())
                .code(domain.getCode())
                .flagUrl(domain.getFlagUrl())
                .createdAt(domain.getCreatedAt())
                .build();
    }

    /**
     * Convierte una lista de entidades JPA a modelos de dominio
     */
    public List<Language> toDomainList(List<LanguageEntity> entities) {
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
    public List<LanguageEntity> toEntityList(List<Language> domains) {
        if (domains == null) {
            return null;
        }

        return domains.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }
}
