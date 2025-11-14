package com.yourteacher.userservice.adapter.in.web;

import com.yourteacher.userservice.adapter.in.web.dto.LoginRequest;
import com.yourteacher.userservice.adapter.in.web.dto.LoginResponse;
import com.yourteacher.userservice.adapter.in.web.dto.OAuthLoginRequest;
import com.yourteacher.userservice.adapter.in.web.dto.UserRequest;
import com.yourteacher.userservice.adapter.in.web.mapper.UserDtoMapper;
import com.yourteacher.userservice.domain.model.User;
import com.yourteacher.userservice.domain.port.in.LoginUseCase;
import com.yourteacher.userservice.domain.port.in.OAuthLoginUseCase;
import com.yourteacher.userservice.domain.port.in.UserService;
import com.yourteacher.userservice.domain.port.out.JwtTokenProvider;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST para operaciones de autenticación
 * Adapter Layer - Primary Adapter (Input)
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final LoginUseCase loginUseCase;
    private final OAuthLoginUseCase oAuthLoginUseCase;
    private final UserService userService;
    private final UserDtoMapper userDtoMapper;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * POST /api/v1/auth/login - Autenticar usuario y generar JWT token
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        log.info("Login request received for email: {}", request.getEmail());

        LoginUseCase.LoginResponse domainResponse = loginUseCase.login(
                request.getEmail(),
                request.getPassword()
        );

        // Convertir la respuesta del dominio a DTO de respuesta
        LoginResponse response = LoginResponse.builder()
                .token(domainResponse.getToken())
                .userId(domainResponse.getUserId())
                .email(domainResponse.getEmail())
                .firstName(domainResponse.getFirstName())
                .lastName(domainResponse.getLastName())
                .build();

        return ResponseEntity.ok(response);
    }

    /**
     * POST /api/v1/auth/register - Registrar nuevo usuario y generar JWT token automáticamente
     */
    @PostMapping("/register")
    public ResponseEntity<LoginResponse> register(@Valid @RequestBody UserRequest request) {
        log.info("Register request received for email: {}", request.getEmail());

        // 1. Registrar usuario
        User user = userDtoMapper.toDomain(request);
        User createdUser = userService.registerUser(user);

        // 2. Generar token JWT automáticamente
        String token = jwtTokenProvider.generateToken(createdUser);

        // 3. Retornar respuesta con token (mismo formato que login)
        LoginResponse response = LoginResponse.builder()
                .token(token)
                .userId(createdUser.getId())
                .email(createdUser.getEmail())
                .firstName(createdUser.getFirstName())
                .lastName(createdUser.getLastName())
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * POST /api/v1/auth/oauth - Autenticar/registrar usuario vía OAuth (Google, etc.)
     * Este endpoint recibe la información del usuario obtenida del proveedor OAuth
     * y crea o actualiza el usuario en el sistema, generando un JWT token
     */
    @PostMapping("/oauth")
    public ResponseEntity<LoginResponse> oauthLogin(@Valid @RequestBody OAuthLoginRequest request) {
        log.info("OAuth login request received for email: {} via provider: {}",
                request.getEmail(), request.getProvider());

        // Convertir DTO a request del dominio
        OAuthLoginUseCase.OAuthLoginRequest domainRequest = OAuthLoginUseCase.OAuthLoginRequest.builder()
                .email(request.getEmail())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .provider(request.getProvider())
                .providerId(request.getProviderId())
                .build();

        // Ejecutar caso de uso
        OAuthLoginUseCase.OAuthLoginResponse domainResponse = oAuthLoginUseCase.loginWithOAuth(domainRequest);

        // Convertir respuesta del dominio a DTO de respuesta
        LoginResponse response = LoginResponse.builder()
                .token(domainResponse.getToken())
                .userId(domainResponse.getUserId())
                .email(domainResponse.getEmail())
                .firstName(domainResponse.getFirstName())
                .lastName(domainResponse.getLastName())
                .build();

        // Retornar 201 si es usuario nuevo, 200 si ya existía
        HttpStatus status = domainResponse.isNewUser() ? HttpStatus.CREATED : HttpStatus.OK;
        return ResponseEntity.status(status).body(response);
    }
}
