package com.yourteacher.userservice.domain.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitarios para la entidad Language del dominio
 */
class LanguageTest {

    @Test
    void shouldCreateLanguageSuccessfully() {
        // Given
        String name = "Spanish";
        String code = "es";
        String flagUrl = "https://flagcdn.com/es.png";

        // When
        Language language = Language.create(name, code, flagUrl);

        // Then
        assertNotNull(language);
        assertEquals(name, language.getName());
        assertEquals(code, language.getCode());
        assertEquals(flagUrl, language.getFlagUrl());
        assertNotNull(language.getCreatedAt());
    }

    @Test
    void shouldNormalizeCodeToLowercase() {
        // Given
        String code = "ES";

        // When
        Language language = Language.create("Spanish", code, "url");

        // Then
        assertEquals("es", language.getCode());
    }

    @Test
    void shouldTrimNameAndCode() {
        // Given
        String name = "  Spanish  ";
        String code = "  es  ";

        // When
        Language language = Language.create(name, code, "url");

        // Then
        assertEquals("Spanish", language.getName());
        assertEquals("es", language.getCode());
    }

    @Test
    void shouldThrowExceptionWhenNameIsNull() {
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            Language.create(null, "es", "url");
        });
    }

    @Test
    void shouldThrowExceptionWhenNameIsEmpty() {
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            Language.create("", "es", "url");
        });

        assertTrue(exception.getMessage().contains("nombre"));
    }

    @Test
    void shouldThrowExceptionWhenNameIsBlank() {
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            Language.create("   ", "es", "url");
        });

        assertTrue(exception.getMessage().contains("nombre"));
    }

    @Test
    void shouldThrowExceptionWhenCodeIsNull() {
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            Language.create("Spanish", null, "url");
        });

        assertTrue(exception.getMessage().contains("código"));
    }

    @Test
    void shouldThrowExceptionWhenCodeHasOneCharacter() {
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            Language.create("Spanish", "e", "url");
        });

        assertTrue(exception.getMessage().contains("2 caracteres"));
    }

    @Test
    void shouldThrowExceptionWhenCodeHasThreeCharacters() {
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            Language.create("Spanish", "esp", "url");
        });

        assertTrue(exception.getMessage().contains("2 caracteres"));
    }

    @Test
    void shouldThrowExceptionWhenCodeHasNumbers() {
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            Language.create("Spanish", "e1", "url");
        });
    }

    @Test
    void shouldThrowExceptionWhenCodeHasSpecialCharacters() {
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            Language.create("Spanish", "e-", "url");
        });
    }

    @Test
    void shouldValidateCorrectName() {
        // Given
        Language language = Language.builder()
                .name("Spanish")
                .code("es")
                .build();

        // When & Then
        assertTrue(language.hasValidName());
    }

    @Test
    void shouldInvalidateNullName() {
        // Given
        Language language = Language.builder()
                .name(null)
                .code("es")
                .build();

        // When & Then
        assertFalse(language.hasValidName());
    }

    @Test
    void shouldInvalidateEmptyName() {
        // Given
        Language language = Language.builder()
                .name("")
                .code("es")
                .build();

        // When & Then
        assertFalse(language.hasValidName());
    }

    @Test
    void shouldInvalidateBlankName() {
        // Given
        Language language = Language.builder()
                .name("   ")
                .code("es")
                .build();

        // When & Then
        assertFalse(language.hasValidName());
    }

    @Test
    void shouldValidateCorrectCode() {
        // Given
        Language language = Language.builder()
                .name("Spanish")
                .code("es")
                .build();

        // When & Then
        assertTrue(language.hasValidCode());
    }

    @Test
    void shouldInvalidateNullCode() {
        // Given
        Language language = Language.builder()
                .name("Spanish")
                .code(null)
                .build();

        // When & Then
        assertFalse(language.hasValidCode());
    }

    @Test
    void shouldInvalidateCodeWithWrongLength() {
        // Given
        Language language = Language.builder()
                .name("Spanish")
                .code("esp")
                .build();

        // When & Then
        assertFalse(language.hasValidCode());
    }

    @Test
    void shouldInvalidateCodeWithUppercase() {
        // Given
        Language language = Language.builder()
                .name("Spanish")
                .code("ES")
                .build();

        // When & Then
        assertFalse(language.hasValidCode());
    }

    @Test
    void shouldValidateCompleteLanguage() {
        // Given
        Language language = Language.create("Spanish", "es", "url");

        // When & Then
        assertTrue(language.isValid());
    }

    @Test
    void shouldInvalidateLanguageWithInvalidName() {
        // Given
        Language language = Language.builder()
                .name("")
                .code("es")
                .build();

        // When & Then
        assertFalse(language.isValid());
    }

    @Test
    void shouldInvalidateLanguageWithInvalidCode() {
        // Given
        Language language = Language.builder()
                .name("Spanish")
                .code("ESP")
                .build();

        // When & Then
        assertFalse(language.isValid());
    }

    @Test
    void shouldCreateNewInstanceWithId() {
        // Given
        Language language = Language.create("Spanish", "es", "url");
        Long newId = 123L;

        // When
        Language languageWithId = language.withId(newId);

        // Then
        assertEquals(newId, languageWithId.getId());
        assertEquals(language.getName(), languageWithId.getName());
        assertEquals(language.getCode(), languageWithId.getCode());
        assertEquals(language.getFlagUrl(), languageWithId.getFlagUrl());
        assertEquals(language.getCreatedAt(), languageWithId.getCreatedAt());
    }

    @Test
    void shouldCreateNewInstanceWithUpdatedFlagUrl() {
        // Given
        Language language = Language.create("Spanish", "es", "url1");
        String newFlagUrl = "https://flagcdn.com/es.png";

        // When
        Language languageWithNewFlag = language.withFlagUrl(newFlagUrl);

        // Then
        assertEquals(newFlagUrl, languageWithNewFlag.getFlagUrl());
        assertEquals(language.getName(), languageWithNewFlag.getName());
        assertEquals(language.getCode(), languageWithNewFlag.getCode());
        assertEquals(language.getCreatedAt(), languageWithNewFlag.getCreatedAt());
    }

    @Test
    void shouldBeImmutable() {
        // Given
        Language original = Language.create("Spanish", "es", "url1");
        String newFlagUrl = "url2";

        // When
        Language modified = original.withFlagUrl(newFlagUrl);

        // Then
        assertNotSame(original, modified);
        assertEquals("url1", original.getFlagUrl());
        assertEquals("url2", modified.getFlagUrl());
    }

    @Test
    void shouldCreateMultipleValidLanguages() {
        // Given & When
        Language spanish = Language.create("Spanish", "es", "url");
        Language english = Language.create("English", "en", "url");
        Language french = Language.create("French", "fr", "url");
        Language german = Language.create("German", "de", "url");

        // Then
        assertTrue(spanish.isValid());
        assertTrue(english.isValid());
        assertTrue(french.isValid());
        assertTrue(german.isValid());
        assertEquals("es", spanish.getCode());
        assertEquals("en", english.getCode());
        assertEquals("fr", french.getCode());
        assertEquals("de", german.getCode());
    }
}
