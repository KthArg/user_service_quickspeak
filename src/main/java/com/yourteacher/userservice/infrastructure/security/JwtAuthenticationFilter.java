package com.yourteacher.userservice.infrastructure.security;

import com.yourteacher.userservice.domain.model.User;
import com.yourteacher.userservice.domain.port.out.JwtTokenProvider;
import com.yourteacher.userservice.domain.port.out.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Filtro de autenticación JWT que intercepta todas las requests HTTP.
 *
 * Responsabilidades:
 * - Extraer el token JWT del header Authorization
 * - Validar el token usando JwtTokenProvider
 * - Establecer la autenticación en el SecurityContext de Spring Security
 *
 * Este filtro se ejecuta una vez por request antes de llegar a los controladores.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        try {
            // 1. Extraer el token JWT del header Authorization
            String token = extractTokenFromRequest(request);

            // 2. Si hay token, validarlo y establecer autenticación
            if (token != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                authenticateToken(token, request);
            }

        } catch (Exception e) {
            log.error("Error processing JWT authentication: {}", e.getMessage());
            // No lanzar excepción - permitir que la request continúe sin autenticación
            // Spring Security manejará los errores de autorización posteriormente
        }

        // 3. Continuar con la cadena de filtros
        filterChain.doFilter(request, response);
    }

    /**
     * Extrae el token JWT del header Authorization
     *
     * @param request HttpServletRequest
     * @return Token JWT sin el prefijo "Bearer ", o null si no existe
     */
    private String extractTokenFromRequest(HttpServletRequest request) {
        String authHeader = request.getHeader(AUTHORIZATION_HEADER);

        if (authHeader != null && authHeader.startsWith(BEARER_PREFIX)) {
            return authHeader.substring(BEARER_PREFIX.length());
        }

        return null;
    }

    /**
     * Valida el token y establece la autenticación en el SecurityContext
     *
     * @param token Token JWT a validar
     * @param request HttpServletRequest para detalles de autenticación
     */
    private void authenticateToken(String token, HttpServletRequest request) {
        // Validar el token
        if (!jwtTokenProvider.validateToken(token)) {
            log.warn("Invalid JWT token");
            return;
        }

        // Extraer email del token
        String email = jwtTokenProvider.getEmailFromToken(token);
        if (email == null) {
            log.warn("Could not extract email from JWT token");
            return;
        }

        // Obtener usuario de la base de datos
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isEmpty()) {
            log.warn("User not found for email: {}", email);
            return;
        }

        User user = userOptional.get();

        // Verificar que el usuario esté activo
        if (!user.isActive()) {
            log.warn("User {} is not active", email);
            return;
        }

        // Crear autenticación de Spring Security
        var authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.name()))
                .collect(Collectors.toList());

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                user,           // Principal (el usuario autenticado)
                null,           // Credentials (no necesarias en JWT)
                authorities     // Authorities/Roles
        );

        // Establecer detalles adicionales de la request
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        // Establecer autenticación en el SecurityContext
        SecurityContextHolder.getContext().setAuthentication(authentication);

        log.debug("User {} authenticated successfully via JWT", email);
    }
}
