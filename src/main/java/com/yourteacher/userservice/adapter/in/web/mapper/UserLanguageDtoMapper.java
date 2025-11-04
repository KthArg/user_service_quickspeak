package com.yourteacher.userservice.adapter.in.web.mapper;

import com.yourteacher.userservice.adapter.in.web.dto.UserLanguageResponse;
import com.yourteacher.userservice.domain.model.Language;
import com.yourteacher.userservice.domain.model.UserLanguage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Mapper para convertir entre UserLanguage del dominio y DTOs
 */
@Component
@RequiredArgsConstructor
public class UserLanguageDtoMapper {

    private final LanguageDtoMapper languageDtoMapper;

    /**
     * Convierte UserLanguage del dominio a UserLanguageResponse
     * Incluye la información completa del idioma
     *
     * @param userLanguage Relación usuario-idioma
     * @param language Información del idioma
     * @return DTO con toda la información
     */
    public UserLanguageResponse toResponse(UserLanguage userLanguage, Language language) {
        if (userLanguage == null) {
            return null;
        }

        return UserLanguageResponse.builder()
                .id(userLanguage.getId())
                .userId(userLanguage.getUserId())
                .language(languageDtoMapper.toResponse(language))
                .isNative(userLanguage.isNative())
                .addedAt(userLanguage.getAddedAt())
                .build();
    }

    /**
     * Convierte una lista de UserLanguage a lista de UserLanguageResponse
     *
     * @param userLanguages Lista de relaciones usuario-idioma
     * @param languagesMap Mapa de languageId -> Language para evitar múltiples queries
     * @return Lista de DTOs
     */
    public List<UserLanguageResponse> toResponseList(List<UserLanguage> userLanguages, Map<Long, Language> languagesMap) {
        if (userLanguages == null) {
            return null;
        }

        return userLanguages.stream()
                .map(ul -> {
                    Language language = languagesMap.get(ul.getLanguageId());
                    return toResponse(ul, language);
                })
                .collect(Collectors.toList());
    }
}
