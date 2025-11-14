package com.yourteacher.userservice.infrastructure.config;

import com.yourteacher.userservice.adapter.out.persistence.entity.LanguageEntity;
import com.yourteacher.userservice.adapter.out.persistence.JpaLanguageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

/**
 * DataLoader - Poblar datos iniciales en la base de datos
 * Carga el catálogo de idiomas disponibles al iniciar la aplicación
 */
@Configuration
@RequiredArgsConstructor
@Slf4j
public class DataLoader {

    private final JpaLanguageRepository languageRepository;

    @Bean
    @Transactional
    CommandLineRunner initDatabase() {
        return args -> {
            // Verificar si ya hay idiomas cargados
            if (languageRepository.count() > 0) {
                log.info("Database already contains {} languages, skipping initialization",
                        languageRepository.count());
                return;
            }

            log.info("Initializing database with language catalog...");

            LocalDateTime now = LocalDateTime.now();

            // Crear lista de idiomas iniciales
            List<LanguageEntity> languages = Arrays.asList(
                // Idiomas populares para aprender
                LanguageEntity.builder()
                        .name("Spanish")
                        .code("es")
                        .flagUrl("https://flagcdn.com/es.svg")
                        .createdAt(now)
                        .build(),

                LanguageEntity.builder()
                        .name("French")
                        .code("fr")
                        .flagUrl("https://flagcdn.com/fr.svg")
                        .createdAt(now)
                        .build(),

                LanguageEntity.builder()
                        .name("German")
                        .code("de")
                        .flagUrl("https://flagcdn.com/de.svg")
                        .createdAt(now)
                        .build(),

                LanguageEntity.builder()
                        .name("Italian")
                        .code("it")
                        .flagUrl("https://flagcdn.com/it.svg")
                        .createdAt(now)
                        .build(),

                LanguageEntity.builder()
                        .name("Portuguese")
                        .code("pt")
                        .flagUrl("https://flagcdn.com/pt.svg")
                        .createdAt(now)
                        .build(),

                LanguageEntity.builder()
                        .name("English")
                        .code("en")
                        .flagUrl("https://flagcdn.com/us.svg")
                        .createdAt(now)
                        .build(),

                LanguageEntity.builder()
                        .name("Chinese")
                        .code("zh")
                        .flagUrl("https://flagcdn.com/cn.svg")
                        .createdAt(now)
                        .build(),

                LanguageEntity.builder()
                        .name("Japanese")
                        .code("ja")
                        .flagUrl("https://flagcdn.com/jp.svg")
                        .createdAt(now)
                        .build(),

                LanguageEntity.builder()
                        .name("Korean")
                        .code("ko")
                        .flagUrl("https://flagcdn.com/kr.svg")
                        .createdAt(now)
                        .build(),

                LanguageEntity.builder()
                        .name("Russian")
                        .code("ru")
                        .flagUrl("https://flagcdn.com/ru.svg")
                        .createdAt(now)
                        .build(),

                LanguageEntity.builder()
                        .name("Arabic")
                        .code("ar")
                        .flagUrl("https://flagcdn.com/sa.svg")
                        .createdAt(now)
                        .build(),

                LanguageEntity.builder()
                        .name("Hindi")
                        .code("hi")
                        .flagUrl("https://flagcdn.com/in.svg")
                        .createdAt(now)
                        .build(),

                LanguageEntity.builder()
                        .name("Dutch")
                        .code("nl")
                        .flagUrl("https://flagcdn.com/nl.svg")
                        .createdAt(now)
                        .build(),

                LanguageEntity.builder()
                        .name("Swedish")
                        .code("sv")
                        .flagUrl("https://flagcdn.com/se.svg")
                        .createdAt(now)
                        .build(),

                LanguageEntity.builder()
                        .name("Norwegian")
                        .code("no")
                        .flagUrl("https://flagcdn.com/no.svg")
                        .createdAt(now)
                        .build(),

                LanguageEntity.builder()
                        .name("Danish")
                        .code("da")
                        .flagUrl("https://flagcdn.com/dk.svg")
                        .createdAt(now)
                        .build(),

                LanguageEntity.builder()
                        .name("Polish")
                        .code("pl")
                        .flagUrl("https://flagcdn.com/pl.svg")
                        .createdAt(now)
                        .build(),

                LanguageEntity.builder()
                        .name("Turkish")
                        .code("tr")
                        .flagUrl("https://flagcdn.com/tr.svg")
                        .createdAt(now)
                        .build(),

                LanguageEntity.builder()
                        .name("Greek")
                        .code("el")
                        .flagUrl("https://flagcdn.com/gr.svg")
                        .createdAt(now)
                        .build(),

                LanguageEntity.builder()
                        .name("Czech")
                        .code("cs")
                        .flagUrl("https://flagcdn.com/cz.svg")
                        .createdAt(now)
                        .build()
            );

            // Guardar todos los idiomas
            languageRepository.saveAll(languages);

            log.info("Successfully initialized {} languages in the database", languages.size());
        };
    }
}
