package com.yourteacher.userservice.adapter.in.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para respuestas con información de idiomas
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LanguageResponse {

    private Long id;
    private String name;
    private String code;
    private String flagUrl;
}
