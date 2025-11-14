-- ============================================
-- SCHEMA SQL PARA USER SERVICE - POSTGRESQL
-- Base de datos: PostgreSQL 13+
-- ============================================

-- Eliminar tablas si existen (orden correcto por FK)
DROP TABLE IF EXISTS user_languages CASCADE;
DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS languages CASCADE;

-- ============================================
-- TABLA: languages
-- Catálogo de idiomas disponibles
-- ============================================
CREATE TABLE languages (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    code VARCHAR(10) NOT NULL UNIQUE,
    native_name VARCHAR(100) NOT NULL,
    flag_emoji VARCHAR(10),
    is_starting_language BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Índices para búsquedas
CREATE INDEX idx_languages_code ON languages(code);
CREATE INDEX idx_languages_is_starting ON languages(is_starting_language);

-- ============================================
-- TABLA: users
-- Información de usuarios registrados
-- ============================================
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,  -- BCrypt hashed
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    avatar_seed VARCHAR(100),  -- Seed para generar avatar
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    role VARCHAR(20) NOT NULL DEFAULT 'USER',  -- USER, ADMIN
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Índices para búsquedas frecuentes
CREATE UNIQUE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_is_active ON users(is_active);
CREATE INDEX idx_users_created_at ON users(created_at DESC);

-- ============================================
-- TABLA: user_languages
-- Relación muchos a muchos entre usuarios e idiomas
-- ============================================
CREATE TABLE user_languages (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    language_id BIGINT NOT NULL,
    is_native BOOLEAN NOT NULL DEFAULT FALSE,
    added_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    -- Foreign Keys
    CONSTRAINT fk_user_languages_user
        FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_user_languages_language
        FOREIGN KEY (language_id)
        REFERENCES languages(id)
        ON DELETE CASCADE,

    -- Constraint: Un usuario no puede tener el mismo idioma dos veces
    CONSTRAINT uk_user_language
        UNIQUE (user_id, language_id)
);

-- Índices para búsquedas frecuentes
CREATE INDEX idx_user_languages_user_id ON user_languages(user_id);
CREATE INDEX idx_user_languages_language_id ON user_languages(language_id);
CREATE INDEX idx_user_languages_is_native ON user_languages(user_id, is_native);

-- ============================================
-- TRIGGERS PARA UPDATED_AT
-- ============================================
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

CREATE TRIGGER update_users_updated_at
    BEFORE UPDATE ON users
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_languages_updated_at
    BEFORE UPDATE ON languages
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

-- ============================================
-- VERIFICACIÓN
-- ============================================
-- SELECT * FROM information_schema.tables WHERE table_schema='public';
-- SELECT column_name, data_type FROM information_schema.columns WHERE table_name='users';

-- ============================================
-- COMENTARIOS EN LAS TABLAS
-- ============================================
COMMENT ON TABLE languages IS 'Catálogo de idiomas disponibles en la plataforma';
COMMENT ON TABLE users IS 'Usuarios registrados en el sistema';
COMMENT ON TABLE user_languages IS 'Idiomas que cada usuario está aprendiendo o domina';

COMMENT ON COLUMN users.password IS 'Password hasheado con BCrypt (nunca en texto plano)';
COMMENT ON COLUMN users.avatar_seed IS 'Seed para generar avatar único del usuario';
COMMENT ON COLUMN user_languages.is_native IS 'Indica si este es el idioma nativo del usuario';
