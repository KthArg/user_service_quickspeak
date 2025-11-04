package com.yourteacher.userservice.adapter.in.web.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para solicitudes de agregar idioma a usuario
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddLanguageRequest {

    @NotNull(message = "Language ID is required")
    private Long languageId;
}
