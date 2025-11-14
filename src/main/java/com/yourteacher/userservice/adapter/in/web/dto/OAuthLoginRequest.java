package com.yourteacher.userservice.adapter.in.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para solicitudes de login vía OAuth (Google, etc.)
 * Contiene la información del usuario obtenida del proveedor OAuth
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OAuthLoginRequest {

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email debe tener un formato válido")
    private String email;

    @NotBlank(message = "El nombre es obligatorio")
    private String firstName;

    @NotBlank(message = "El apellido es obligatorio")
    private String lastName;

    /**
     * Proveedor OAuth (google, facebook, etc.)
     */
    @NotBlank(message = "El proveedor OAuth es obligatorio")
    private String provider;

    /**
     * ID del usuario en el proveedor OAuth
     */
    @NotBlank(message = "El ID del proveedor es obligatorio")
    private String providerId;
}
