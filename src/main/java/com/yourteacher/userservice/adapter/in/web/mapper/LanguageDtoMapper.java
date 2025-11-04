package com.yourteacher.userservice.adapter.in.web.mapper;

import com.yourteacher.userservice.adapter.in.web.dto.LanguageResponse;
import com.yourteacher.userservice.domain.model.Language;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper para convertir entre Language del dominio y DTOs
 */
@Component
public class LanguageDtoMapper {

    /**
     * Convierte Language del dominio a LanguageResponse
     */
    public LanguageResponse toResponse(Language language) {
        if (language == null) {
            return null;
        }

        return LanguageResponse.builder()
                .id(language.getId())
                .name(language.getName())
                .code(language.getCode())
                .flagUrl(language.getFlagUrl())
                .build();
    }

    /**
     * Convierte una lista de Language a lista de LanguageResponse
     */
    public List<LanguageResponse> toResponseList(List<Language> languages) {
        if (languages == null) {
            return null;
        }

        return languages.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
}
