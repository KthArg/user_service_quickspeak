-- ============================================
-- SCHEMA SQL PARA USER SERVICE
-- Base de datos: Azure SQL Database / PostgreSQL
-- ============================================

-- Eliminar tablas si existen (orden correcto por FK)
DROP TABLE IF EXISTS user_languages;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS languages;

-- ============================================
-- TABLA: languages
-- Catálogo de idiomas disponibles
-- ============================================
CREATE TABLE languages (
    id BIGINT PRIMARY KEY IDENTITY(1,1),
    name NVARCHAR(100) NOT NULL UNIQUE,
    code NVARCHAR(10) NOT NULL UNIQUE,
    native_name NVARCHAR(100) NOT NULL,
    flag_emoji NVARCHAR(10),
    is_starting_language BIT NOT NULL DEFAULT 0,
    created_at DATETIME2 NOT NULL DEFAULT GETDATE(),
    updated_at DATETIME2 NOT NULL DEFAULT GETDATE()
);

-- Índices para búsquedas
CREATE INDEX idx_languages_code ON languages(code);
CREATE INDEX idx_languages_is_starting ON languages(is_starting_language);

-- ============================================
-- TABLA: users
-- Información de usuarios registrados
-- ============================================
CREATE TABLE users (
    id BIGINT PRIMARY KEY IDENTITY(1,1),
    email NVARCHAR(255) NOT NULL UNIQUE,
    password NVARCHAR(255) NOT NULL,  -- BCrypt hashed
    first_name NVARCHAR(100) NOT NULL,
    last_name NVARCHAR(100) NOT NULL,
    avatar_seed NVARCHAR(100),  -- Seed para generar avatar
    is_active BIT NOT NULL DEFAULT 1,
    role NVARCHAR(20) NOT NULL DEFAULT 'USER',  -- USER, ADMIN
    created_at DATETIME2 NOT NULL DEFAULT GETDATE(),
    updated_at DATETIME2 NOT NULL DEFAULT GETDATE()
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
    id BIGINT PRIMARY KEY IDENTITY(1,1),
    user_id BIGINT NOT NULL,
    language_id BIGINT NOT NULL,
    is_native BIT NOT NULL DEFAULT 0,
    added_at DATETIME2 NOT NULL DEFAULT GETDATE(),

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
-- TRIGGERS PARA UPDATED_AT (Opcional)
-- ============================================
-- Para PostgreSQL, usar esta sintaxis:
-- CREATE OR REPLACE FUNCTION update_updated_at_column()
-- RETURNS TRIGGER AS $$
-- BEGIN
--     NEW.updated_at = NOW();
--     RETURN NEW;
-- END;
-- $$ language 'plpgsql';

-- CREATE TRIGGER update_users_updated_at BEFORE UPDATE ON users
--     FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- CREATE TRIGGER update_languages_updated_at BEFORE UPDATE ON languages
--     FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- ============================================
-- VERIFICACIÓN
-- ============================================
-- SELECT * FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_TYPE='BASE TABLE';
-- SELECT * FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME='users';
