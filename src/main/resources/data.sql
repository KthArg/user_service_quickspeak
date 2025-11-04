-- Script de inicialización de datos para H2 (desarrollo)
-- Este archivo se ejecuta automáticamente al iniciar la aplicación en modo desarrollo

-- ============================================================
-- CATÁLOGO DE IDIOMAS (Languages)
-- ============================================================
-- Top 10 idiomas más hablados y estudiados del mundo
-- Códigos según ISO 639-1 (2 letras en minúsculas)

INSERT INTO languages (name, code, flag_url, created_at) VALUES
('English', 'en', 'https://flagcdn.com/w320/gb.png', CURRENT_TIMESTAMP),
('Spanish', 'es', 'https://flagcdn.com/w320/es.png', CURRENT_TIMESTAMP),
('French', 'fr', 'https://flagcdn.com/w320/fr.png', CURRENT_TIMESTAMP),
('German', 'de', 'https://flagcdn.com/w320/de.png', CURRENT_TIMESTAMP),
('Portuguese', 'pt', 'https://flagcdn.com/w320/pt.png', CURRENT_TIMESTAMP),
('Italian', 'it', 'https://flagcdn.com/w320/it.png', CURRENT_TIMESTAMP),
('Japanese', 'ja', 'https://flagcdn.com/w320/jp.png', CURRENT_TIMESTAMP),
('Korean', 'ko', 'https://flagcdn.com/w320/kr.png', CURRENT_TIMESTAMP),
('Chinese', 'zh', 'https://flagcdn.com/w320/cn.png', CURRENT_TIMESTAMP),
('Russian', 'ru', 'https://flagcdn.com/w320/ru.png', CURRENT_TIMESTAMP);

-- Idiomas adicionales populares
INSERT INTO languages (name, code, flag_url, created_at) VALUES
('Arabic', 'ar', 'https://flagcdn.com/w320/sa.png', CURRENT_TIMESTAMP),
('Hindi', 'hi', 'https://flagcdn.com/w320/in.png', CURRENT_TIMESTAMP),
('Dutch', 'nl', 'https://flagcdn.com/w320/nl.png', CURRENT_TIMESTAMP),
('Polish', 'pl', 'https://flagcdn.com/w320/pl.png', CURRENT_TIMESTAMP),
('Turkish', 'tr', 'https://flagcdn.com/w320/tr.png', CURRENT_TIMESTAMP),
('Swedish', 'sv', 'https://flagcdn.com/w320/se.png', CURRENT_TIMESTAMP),
('Greek', 'el', 'https://flagcdn.com/w320/gr.png', CURRENT_TIMESTAMP),
('Czech', 'cs', 'https://flagcdn.com/w320/cz.png', CURRENT_TIMESTAMP),
('Danish', 'da', 'https://flagcdn.com/w320/dk.png', CURRENT_TIMESTAMP),
('Finnish', 'fi', 'https://flagcdn.com/w320/fi.png', CURRENT_TIMESTAMP);

-- ============================================================
-- USUARIOS DE PRUEBA (opcional - comentar en producción)
-- ============================================================

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

-- ============================================================
-- RELACIONES USUARIO-IDIOMA DE EJEMPLO
-- ============================================================

-- María (student) aprende inglés y francés, su idioma nativo es español
INSERT INTO user_languages (user_id, language_id, is_native, added_at) VALUES
((SELECT id FROM users WHERE email = 'student@quickspeak.com'), (SELECT id FROM languages WHERE code = 'es'), true, CURRENT_TIMESTAMP),
((SELECT id FROM users WHERE email = 'student@quickspeak.com'), (SELECT id FROM languages WHERE code = 'en'), false, CURRENT_TIMESTAMP),
((SELECT id FROM users WHERE email = 'student@quickspeak.com'), (SELECT id FROM languages WHERE code = 'fr'), false, CURRENT_TIMESTAMP);

-- Ana (admin) su idioma nativo es español, aprende inglés
INSERT INTO user_languages (user_id, language_id, is_native, added_at) VALUES
((SELECT id FROM users WHERE email = 'admin@quickspeak.com'), (SELECT id FROM languages WHERE code = 'es'), true, CURRENT_TIMESTAMP),
((SELECT id FROM users WHERE email = 'admin@quickspeak.com'), (SELECT id FROM languages WHERE code = 'en'), false, CURRENT_TIMESTAMP);
