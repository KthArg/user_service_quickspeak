-- Script de inicialización de datos para H2 (desarrollo)
-- Este archivo se ejecuta automáticamente al iniciar la aplicación en modo desarrollo

-- Insertar usuarios de prueba (contraseñas encriptadas con BCrypt)
-- Nota: En producción esto no se ejecutará (DDL_AUTO=validate)

-- Usuario Estudiante (password: student123)
-- BCrypt hash generado para "student123"
INSERT INTO users (email, password, first_name, last_name, status, created_at, updated_at)
VALUES ('student@quickspeak.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'María', 'González', 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO user_roles (user_id, role) VALUES ((SELECT id FROM users WHERE email = 'student@quickspeak.com'), 'STUDENT');

-- Usuario Admin (password: admin123)
-- BCrypt hash generado para "admin123"
INSERT INTO users (email, password, first_name, last_name, status, created_at, updated_at)
VALUES ('admin@quickspeak.com', '$2a$10$A/lRwYlJ1rW6dKZvCr7WduGVNnz.eDvKvKvbC0VKOvKvBJVo8nxS2', 'Ana', 'Martínez', 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO user_roles (user_id, role) VALUES ((SELECT id FROM users WHERE email = 'admin@quickspeak.com'), 'ADMIN');
