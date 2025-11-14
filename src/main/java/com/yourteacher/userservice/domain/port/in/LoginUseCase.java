package com.yourteacher.userservice.domain.port.in;

/**
 * Puerto de entrada (Input Port) para autenticación de usuarios
 * Define el caso de uso de login con credenciales
 * (Hexagonal Architecture - Primary Port)
 *
 * Este caso de uso permite:
 * - Autenticar usuarios con email y password
 * - Generar JWT tokens para sesiones
 */
public interface LoginUseCase {

    /**
     * Autentica un usuario con sus credenciales y genera un JWT token
     *
     * Precondiciones:
     * - El email debe existir en el sistema
     * - El password debe ser correcto
     * - El usuario debe estar activo
     *
     * @param email Email del usuario
     * @param password Password en texto plano
     * @return LoginResponse con el token JWT y datos del usuario
     * @throws InvalidCredentialsException si las credenciales son inválidas
     */
    LoginResponse login(String email, String password);

    /**
     * Clase interna para la respuesta del login
     */
    class LoginResponse {
        private final String token;
        private final Long userId;
        private final String email;
        private final String firstName;
        private final String lastName;

        public LoginResponse(String token, Long userId, String email, String firstName, String lastName) {
            this.token = token;
            this.userId = userId;
            this.email = email;
            this.firstName = firstName;
            this.lastName = lastName;
        }

        public String getToken() {
            return token;
        }

        public Long getUserId() {
            return userId;
        }

        public String getEmail() {
            return email;
        }

        public String getFirstName() {
            return firstName;
        }

        public String getLastName() {
            return lastName;
        }
    }
}
