package com.yourteacher.userservice.adapter.in.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO para respuestas con información de idiomas de usuario
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserLanguageResponse {

    private Long id;
    private Long userId;
    private LanguageResponse language;
    private boolean isNative;
    private LocalDateTime addedAt;
}
