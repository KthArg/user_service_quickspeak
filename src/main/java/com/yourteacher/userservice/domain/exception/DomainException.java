package com.yourteacher.userservice.domain.exception;

/**
 * Clase base abstracta para todas las excepciones del dominio
 * (Hexagonal Architecture - Domain Layer)
 *
 * Todas las excepciones de negocio deben heredar de esta clase
 * para mantener una jerarquía clara y facilitar el manejo de errores.
 *
 * Esta excepción es RuntimeException porque representa violaciones
 * de reglas de negocio que no deberían ser recuperables en el flujo normal.
 */
public abstract class DomainException extends RuntimeException {

    /**
     * Constructor con mensaje de error
     *
     * @param message Descripción del error de dominio
     */
    public DomainException(String message) {
        super(message);
    }

    /**
     * Constructor con mensaje de error y causa
     *
     * @param message Descripción del error de dominio
     * @param cause Excepción que causó este error
     */
    public DomainException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor con causa
     *
     * @param cause Excepción que causó este error
     */
    public DomainException(Throwable cause) {
        super(cause);
    }
}
