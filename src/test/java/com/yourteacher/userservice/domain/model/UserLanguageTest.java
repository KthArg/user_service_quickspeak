package com.yourteacher.userservice.domain.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitarios para la entidad UserLanguage del dominio
 */
class UserLanguageTest {

    @Test
    void shouldCreateUserLanguageAsLearning() {
        // Given
        Long userId = 1L;
        Long languageId = 2L;

        // When
        UserLanguage userLanguage = UserLanguage.create(userId, languageId, false);

        // Then
        assertNotNull(userLanguage);
        assertEquals(userId, userLanguage.getUserId());
        assertEquals(languageId, userLanguage.getLanguageId());
        assertFalse(userLanguage.isNative());
        assertTrue(userLanguage.isLearningLanguage());
        assertFalse(userLanguage.isNativeLanguage());
        assertNotNull(userLanguage.getAddedAt());
    }

    @Test
    void shouldCreateUserLanguageAsNative() {
        // Given
        Long userId = 1L;
        Long languageId = 2L;

        // When
        UserLanguage userLanguage = UserLanguage.create(userId, languageId, true);

        // Then
        assertNotNull(userLanguage);
        assertEquals(userId, userLanguage.getUserId());
        assertEquals(languageId, userLanguage.getLanguageId());
        assertTrue(userLanguage.isNative());
        assertTrue(userLanguage.isNativeLanguage());
        assertFalse(userLanguage.isLearningLanguage());
        assertNotNull(userLanguage.getAddedAt());
    }

    @Test
    void shouldCreateNativeLanguageUsingFactoryMethod() {
        // Given
        Long userId = 1L;
        Long languageId = 2L;

        // When
        UserLanguage userLanguage = UserLanguage.createNative(userId, languageId);

        // Then
        assertTrue(userLanguage.isNative());
        assertTrue(userLanguage.isNativeLanguage());
        assertFalse(userLanguage.isLearningLanguage());
    }

    @Test
    void shouldCreateLearningLanguageUsingFactoryMethod() {
        // Given
        Long userId = 1L;
        Long languageId = 2L;

        // When
        UserLanguage userLanguage = UserLanguage.createLearning(userId, languageId);

        // Then
        assertFalse(userLanguage.isNative());
        assertFalse(userLanguage.isNativeLanguage());
        assertTrue(userLanguage.isLearningLanguage());
    }

    @Test
    void shouldMarkAsNative() {
        // Given
        UserLanguage learning = UserLanguage.createLearning(1L, 2L);

        // When
        UserLanguage native_ = learning.markAsNative();

        // Then
        assertTrue(native_.isNative());
        assertTrue(native_.isNativeLanguage());
        assertEquals(learning.getUserId(), native_.getUserId());
        assertEquals(learning.getLanguageId(), native_.getLanguageId());
        assertEquals(learning.getAddedAt(), native_.getAddedAt());
    }

    @Test
    void shouldMarkAsLearning() {
        // Given
        UserLanguage native_ = UserLanguage.createNative(1L, 2L);

        // When
        UserLanguage learning = native_.markAsLearning();

        // Then
        assertFalse(learning.isNative());
        assertTrue(learning.isLearningLanguage());
        assertEquals(native_.getUserId(), learning.getUserId());
        assertEquals(native_.getLanguageId(), learning.getLanguageId());
        assertEquals(native_.getAddedAt(), learning.getAddedAt());
    }

    @Test
    void shouldBeImmutableWhenMarkingAsNative() {
        // Given
        UserLanguage original = UserLanguage.createLearning(1L, 2L);

        // When
        UserLanguage modified = original.markAsNative();

        // Then
        assertNotSame(original, modified);
        assertFalse(original.isNative());
        assertTrue(modified.isNative());
    }

    @Test
    void shouldBeImmutableWhenMarkingAsLearning() {
        // Given
        UserLanguage original = UserLanguage.createNative(1L, 2L);

        // When
        UserLanguage modified = original.markAsLearning();

        // Then
        assertNotSame(original, modified);
        assertTrue(original.isNative());
        assertFalse(modified.isNative());
    }

    @Test
    void shouldThrowExceptionWhenUserIdIsNull() {
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            UserLanguage.create(null, 2L, false);
        });

        assertTrue(exception.getMessage().contains("usuario"));
    }

    @Test
    void shouldThrowExceptionWhenUserIdIsZero() {
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            UserLanguage.create(0L, 2L, false);
        });

        assertTrue(exception.getMessage().contains("usuario"));
    }

    @Test
    void shouldThrowExceptionWhenUserIdIsNegative() {
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            UserLanguage.create(-1L, 2L, false);
        });

        assertTrue(exception.getMessage().contains("usuario"));
    }

    @Test
    void shouldThrowExceptionWhenLanguageIdIsNull() {
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            UserLanguage.create(1L, null, false);
        });

        assertTrue(exception.getMessage().contains("idioma"));
    }

    @Test
    void shouldThrowExceptionWhenLanguageIdIsZero() {
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            UserLanguage.create(1L, 0L, false);
        });

        assertTrue(exception.getMessage().contains("idioma"));
    }

    @Test
    void shouldThrowExceptionWhenLanguageIdIsNegative() {
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            UserLanguage.create(1L, -1L, false);
        });

        assertTrue(exception.getMessage().contains("idioma"));
    }

    @Test
    void shouldValidateCorrectUserLanguage() {
        // Given
        UserLanguage userLanguage = UserLanguage.create(1L, 2L, false);

        // When & Then
        assertTrue(userLanguage.isValid());
    }

    @Test
    void shouldInvalidateUserLanguageWithNullUserId() {
        // Given
        UserLanguage userLanguage = UserLanguage.builder()
                .userId(null)
                .languageId(2L)
                .isNative(false)
                .build();

        // When & Then
        assertFalse(userLanguage.isValid());
    }

    @Test
    void shouldInvalidateUserLanguageWithZeroUserId() {
        // Given
        UserLanguage userLanguage = UserLanguage.builder()
                .userId(0L)
                .languageId(2L)
                .isNative(false)
                .build();

        // When & Then
        assertFalse(userLanguage.isValid());
    }

    @Test
    void shouldInvalidateUserLanguageWithNegativeUserId() {
        // Given
        UserLanguage userLanguage = UserLanguage.builder()
                .userId(-1L)
                .languageId(2L)
                .isNative(false)
                .build();

        // When & Then
        assertFalse(userLanguage.isValid());
    }

    @Test
    void shouldInvalidateUserLanguageWithNullLanguageId() {
        // Given
        UserLanguage userLanguage = UserLanguage.builder()
                .userId(1L)
                .languageId(null)
                .isNative(false)
                .build();

        // When & Then
        assertFalse(userLanguage.isValid());
    }

    @Test
    void shouldInvalidateUserLanguageWithZeroLanguageId() {
        // Given
        UserLanguage userLanguage = UserLanguage.builder()
                .userId(1L)
                .languageId(0L)
                .isNative(false)
                .build();

        // When & Then
        assertFalse(userLanguage.isValid());
    }

    @Test
    void shouldInvalidateUserLanguageWithNegativeLanguageId() {
        // Given
        UserLanguage userLanguage = UserLanguage.builder()
                .userId(1L)
                .languageId(-1L)
                .isNative(false)
                .build();

        // When & Then
        assertFalse(userLanguage.isValid());
    }

    @Test
    void shouldCreateNewInstanceWithId() {
        // Given
        UserLanguage userLanguage = UserLanguage.create(1L, 2L, false);
        Long newId = 123L;

        // When
        UserLanguage userLanguageWithId = userLanguage.withId(newId);

        // Then
        assertEquals(newId, userLanguageWithId.getId());
        assertEquals(userLanguage.getUserId(), userLanguageWithId.getUserId());
        assertEquals(userLanguage.getLanguageId(), userLanguageWithId.getLanguageId());
        assertEquals(userLanguage.isNative(), userLanguageWithId.isNative());
        assertEquals(userLanguage.getAddedAt(), userLanguageWithId.getAddedAt());
    }

    @Test
    void shouldBeImmutableWhenAddingId() {
        // Given
        UserLanguage original = UserLanguage.create(1L, 2L, false);
        Long newId = 123L;

        // When
        UserLanguage modified = original.withId(newId);

        // Then
        assertNotSame(original, modified);
        assertNull(original.getId());
        assertEquals(newId, modified.getId());
    }

    @Test
    void shouldCreateMultipleDifferentUserLanguages() {
        // Given & When
        UserLanguage ul1 = UserLanguage.createLearning(1L, 2L);
        UserLanguage ul2 = UserLanguage.createLearning(1L, 3L);
        UserLanguage ul3 = UserLanguage.createNative(2L, 1L);

        // Then
        assertTrue(ul1.isValid());
        assertTrue(ul2.isValid());
        assertTrue(ul3.isValid());
        assertEquals(1L, ul1.getUserId());
        assertEquals(1L, ul2.getUserId());
        assertEquals(2L, ul3.getUserId());
        assertFalse(ul1.isNative());
        assertFalse(ul2.isNative());
        assertTrue(ul3.isNative());
    }

    @Test
    void shouldToggleBetweenNativeAndLearning() {
        // Given
        UserLanguage learning = UserLanguage.createLearning(1L, 2L);

        // When
        UserLanguage native_ = learning.markAsNative();
        UserLanguage backToLearning = native_.markAsLearning();
        UserLanguage backToNative = backToLearning.markAsNative();

        // Then
        assertFalse(learning.isNative());
        assertTrue(native_.isNative());
        assertFalse(backToLearning.isNative());
        assertTrue(backToNative.isNative());
    }
}
