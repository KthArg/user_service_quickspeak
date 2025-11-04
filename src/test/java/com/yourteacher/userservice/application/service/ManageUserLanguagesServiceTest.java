package com.yourteacher.userservice.application.service;

import com.yourteacher.userservice.domain.exception.LanguageAlreadyAddedException;
import com.yourteacher.userservice.domain.exception.LanguageNotAddedToUserException;
import com.yourteacher.userservice.domain.exception.LanguageNotFoundException;
import com.yourteacher.userservice.domain.exception.NativeLanguageCannotBeRemovedException;
import com.yourteacher.userservice.domain.exception.UserNotFoundException;
import com.yourteacher.userservice.domain.model.Language;
import com.yourteacher.userservice.domain.model.User;
import com.yourteacher.userservice.domain.model.UserLanguage;
import com.yourteacher.userservice.domain.port.out.LanguageRepository;
import com.yourteacher.userservice.domain.port.out.UserLanguageRepository;
import com.yourteacher.userservice.domain.port.out.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios para ManageUserLanguagesService
 * Utiliza mocks para las dependencias (repositorios)
 */
@ExtendWith(MockitoExtension.class)
class ManageUserLanguagesServiceTest {

    @Mock
    private UserLanguageRepository userLanguageRepository;

    @Mock
    private LanguageRepository languageRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ManageUserLanguagesService service;

    // ==================== addLanguageToUser Tests ====================

    @Test
    void shouldAddLanguageToUserSuccessfully() {
        // Given
        Long userId = 1L;
        Long languageId = 2L;
        User mockUser = User.builder().id(userId).build();
        Language mockLanguage = Language.builder().id(languageId).build();
        UserLanguage expectedUserLanguage = UserLanguage.createLearning(userId, languageId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(languageRepository.findById(languageId)).thenReturn(Optional.of(mockLanguage));
        when(userLanguageRepository.existsByUserIdAndLanguageId(userId, languageId)).thenReturn(false);
        when(userLanguageRepository.save(any(UserLanguage.class))).thenReturn(expectedUserLanguage);

        // When
        UserLanguage result = service.addLanguageToUser(userId, languageId);

        // Then
        assertNotNull(result);
        assertEquals(userId, result.getUserId());
        assertEquals(languageId, result.getLanguageId());
        assertFalse(result.isNative());
        verify(userRepository).findById(userId);
        verify(languageRepository).findById(languageId);
        verify(userLanguageRepository).existsByUserIdAndLanguageId(userId, languageId);
        verify(userLanguageRepository).save(any(UserLanguage.class));
    }

    @Test
    void shouldThrowExceptionWhenAddingLanguageToNonExistentUser() {
        // Given
        Long userId = 1L;
        Long languageId = 2L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(UserNotFoundException.class, () -> {
            service.addLanguageToUser(userId, languageId);
        });

        verify(userRepository).findById(userId);
        verify(languageRepository, never()).findById(anyLong());
        verify(userLanguageRepository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenAddingNonExistentLanguage() {
        // Given
        Long userId = 1L;
        Long languageId = 2L;
        User mockUser = User.builder().id(userId).build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(languageRepository.findById(languageId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(LanguageNotFoundException.class, () -> {
            service.addLanguageToUser(userId, languageId);
        });

        verify(userRepository).findById(userId);
        verify(languageRepository).findById(languageId);
        verify(userLanguageRepository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenLanguageAlreadyAdded() {
        // Given
        Long userId = 1L;
        Long languageId = 2L;
        User mockUser = User.builder().id(userId).build();
        Language mockLanguage = Language.builder().id(languageId).build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(languageRepository.findById(languageId)).thenReturn(Optional.of(mockLanguage));
        when(userLanguageRepository.existsByUserIdAndLanguageId(userId, languageId)).thenReturn(true);

        // When & Then
        assertThrows(LanguageAlreadyAddedException.class, () -> {
            service.addLanguageToUser(userId, languageId);
        });

        verify(userLanguageRepository, never()).save(any());
    }

    // ==================== setNativeLanguage Tests ====================

    @Test
    void shouldSetNativeLanguageSuccessfully() {
        // Given
        Long userId = 1L;
        Long languageId = 2L;
        User mockUser = User.builder().id(userId).build();
        Language mockLanguage = Language.builder().id(languageId).build();
        UserLanguage existingUserLanguage = UserLanguage.createLearning(userId, languageId).withId(10L);
        UserLanguage nativeUserLanguage = existingUserLanguage.markAsNative();

        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(languageRepository.findById(languageId)).thenReturn(Optional.of(mockLanguage));
        when(userLanguageRepository.findByUserIdAndLanguageId(userId, languageId))
                .thenReturn(Optional.of(existingUserLanguage));
        when(userLanguageRepository.findNativeLanguageByUserId(userId)).thenReturn(Optional.empty());
        when(userLanguageRepository.save(any(UserLanguage.class))).thenReturn(nativeUserLanguage);

        // When
        UserLanguage result = service.setNativeLanguage(userId, languageId);

        // Then
        assertNotNull(result);
        assertTrue(result.isNative());
        verify(userRepository).findById(userId);
        verify(languageRepository).findById(languageId);
        verify(userLanguageRepository).findByUserIdAndLanguageId(userId, languageId);
        verify(userLanguageRepository).save(any(UserLanguage.class));
    }

    @Test
    void shouldUnmarkPreviousNativeLanguageWhenSettingNewNative() {
        // Given
        Long userId = 1L;
        Long oldNativeLanguageId = 2L;
        Long newNativeLanguageId = 3L;

        User mockUser = User.builder().id(userId).build();
        Language mockLanguage = Language.builder().id(newNativeLanguageId).build();

        UserLanguage oldNativeLanguage = UserLanguage.createNative(userId, oldNativeLanguageId).withId(10L);
        UserLanguage newLanguageToMakeNative = UserLanguage.createLearning(userId, newNativeLanguageId).withId(11L);

        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(languageRepository.findById(newNativeLanguageId)).thenReturn(Optional.of(mockLanguage));
        when(userLanguageRepository.findByUserIdAndLanguageId(userId, newNativeLanguageId))
                .thenReturn(Optional.of(newLanguageToMakeNative));
        when(userLanguageRepository.findNativeLanguageByUserId(userId))
                .thenReturn(Optional.of(oldNativeLanguage));
        when(userLanguageRepository.save(any(UserLanguage.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // When
        service.setNativeLanguage(userId, newNativeLanguageId);

        // Then
        verify(userLanguageRepository, times(2)).save(any(UserLanguage.class));
    }

    @Test
    void shouldThrowExceptionWhenSettingNativeLanguageForNonExistentUser() {
        // Given
        Long userId = 1L;
        Long languageId = 2L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(UserNotFoundException.class, () -> {
            service.setNativeLanguage(userId, languageId);
        });

        verify(userLanguageRepository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenSettingNonExistentLanguageAsNative() {
        // Given
        Long userId = 1L;
        Long languageId = 2L;
        User mockUser = User.builder().id(userId).build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(languageRepository.findById(languageId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(LanguageNotFoundException.class, () -> {
            service.setNativeLanguage(userId, languageId);
        });

        verify(userLanguageRepository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenSettingLanguageNotAddedToUserAsNative() {
        // Given
        Long userId = 1L;
        Long languageId = 2L;
        User mockUser = User.builder().id(userId).build();
        Language mockLanguage = Language.builder().id(languageId).build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(languageRepository.findById(languageId)).thenReturn(Optional.of(mockLanguage));
        when(userLanguageRepository.findByUserIdAndLanguageId(userId, languageId))
                .thenReturn(Optional.empty());

        // When & Then
        assertThrows(LanguageNotAddedToUserException.class, () -> {
            service.setNativeLanguage(userId, languageId);
        });

        verify(userLanguageRepository, never()).save(any());
    }

    // ==================== removeLanguageFromUser Tests ====================

    @Test
    void shouldRemoveLanguageFromUserSuccessfully() {
        // Given
        Long userId = 1L;
        Long languageId = 2L;
        User mockUser = User.builder().id(userId).build();
        UserLanguage learningLanguage = UserLanguage.createLearning(userId, languageId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(userLanguageRepository.findByUserIdAndLanguageId(userId, languageId))
                .thenReturn(Optional.of(learningLanguage));

        // When
        service.removeLanguageFromUser(userId, languageId);

        // Then
        verify(userRepository).findById(userId);
        verify(userLanguageRepository).findByUserIdAndLanguageId(userId, languageId);
        verify(userLanguageRepository).deleteByUserIdAndLanguageId(userId, languageId);
    }

    @Test
    void shouldNotFailWhenRemovingNonExistentLanguage() {
        // Given
        Long userId = 1L;
        Long languageId = 2L;
        User mockUser = User.builder().id(userId).build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(userLanguageRepository.findByUserIdAndLanguageId(userId, languageId))
                .thenReturn(Optional.empty());

        // When
        service.removeLanguageFromUser(userId, languageId);

        // Then
        verify(userLanguageRepository, never()).deleteByUserIdAndLanguageId(anyLong(), anyLong());
    }

    @Test
    void shouldThrowExceptionWhenRemovingNativeLanguage() {
        // Given
        Long userId = 1L;
        Long languageId = 2L;
        User mockUser = User.builder().id(userId).build();
        UserLanguage nativeLanguage = UserLanguage.createNative(userId, languageId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(userLanguageRepository.findByUserIdAndLanguageId(userId, languageId))
                .thenReturn(Optional.of(nativeLanguage));

        // When & Then
        assertThrows(NativeLanguageCannotBeRemovedException.class, () -> {
            service.removeLanguageFromUser(userId, languageId);
        });

        verify(userLanguageRepository, never()).deleteByUserIdAndLanguageId(anyLong(), anyLong());
    }

    @Test
    void shouldThrowExceptionWhenRemovingLanguageFromNonExistentUser() {
        // Given
        Long userId = 1L;
        Long languageId = 2L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(UserNotFoundException.class, () -> {
            service.removeLanguageFromUser(userId, languageId);
        });

        verify(userLanguageRepository, never()).deleteByUserIdAndLanguageId(anyLong(), anyLong());
    }

    // ==================== getUserLanguages Tests ====================

    @Test
    void shouldGetUserLanguagesSuccessfully() {
        // Given
        Long userId = 1L;
        User mockUser = User.builder().id(userId).build();
        List<UserLanguage> languages = List.of(
                UserLanguage.createNative(userId, 1L),
                UserLanguage.createLearning(userId, 2L),
                UserLanguage.createLearning(userId, 3L)
        );

        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(userLanguageRepository.findByUserId(userId)).thenReturn(languages);

        // When
        List<UserLanguage> result = service.getUserLanguages(userId);

        // Then
        assertNotNull(result);
        assertEquals(3, result.size());
        verify(userRepository).findById(userId);
        verify(userLanguageRepository).findByUserId(userId);
    }

    @Test
    void shouldReturnEmptyListWhenUserHasNoLanguages() {
        // Given
        Long userId = 1L;
        User mockUser = User.builder().id(userId).build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(userLanguageRepository.findByUserId(userId)).thenReturn(List.of());

        // When
        List<UserLanguage> result = service.getUserLanguages(userId);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void shouldThrowExceptionWhenGettingLanguagesForNonExistentUser() {
        // Given
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(UserNotFoundException.class, () -> {
            service.getUserLanguages(userId);
        });

        verify(userLanguageRepository, never()).findByUserId(anyLong());
    }

    // ==================== getNativeLanguage Tests ====================

    @Test
    void shouldGetNativeLanguageSuccessfully() {
        // Given
        Long userId = 1L;
        Long languageId = 2L;
        User mockUser = User.builder().id(userId).build();
        UserLanguage nativeLanguage = UserLanguage.createNative(userId, languageId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(userLanguageRepository.findNativeLanguageByUserId(userId))
                .thenReturn(Optional.of(nativeLanguage));

        // When
        UserLanguage result = service.getNativeLanguage(userId);

        // Then
        assertNotNull(result);
        assertTrue(result.isNative());
        assertEquals(languageId, result.getLanguageId());
    }

    @Test
    void shouldReturnNullWhenUserHasNoNativeLanguage() {
        // Given
        Long userId = 1L;
        User mockUser = User.builder().id(userId).build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(userLanguageRepository.findNativeLanguageByUserId(userId))
                .thenReturn(Optional.empty());

        // When
        UserLanguage result = service.getNativeLanguage(userId);

        // Then
        assertNull(result);
    }

    @Test
    void shouldThrowExceptionWhenGettingNativeLanguageForNonExistentUser() {
        // Given
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(UserNotFoundException.class, () -> {
            service.getNativeLanguage(userId);
        });
    }

    // ==================== getLearningLanguages Tests ====================

    @Test
    void shouldGetLearningLanguagesSuccessfully() {
        // Given
        Long userId = 1L;
        User mockUser = User.builder().id(userId).build();
        List<UserLanguage> learningLanguages = List.of(
                UserLanguage.createLearning(userId, 2L),
                UserLanguage.createLearning(userId, 3L)
        );

        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(userLanguageRepository.findLearningLanguagesByUserId(userId))
                .thenReturn(learningLanguages);

        // When
        List<UserLanguage> result = service.getLearningLanguages(userId);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(UserLanguage::isLearningLanguage));
    }

    @Test
    void shouldReturnEmptyListWhenUserHasNoLearningLanguages() {
        // Given
        Long userId = 1L;
        User mockUser = User.builder().id(userId).build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(userLanguageRepository.findLearningLanguagesByUserId(userId))
                .thenReturn(List.of());

        // When
        List<UserLanguage> result = service.getLearningLanguages(userId);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void shouldThrowExceptionWhenGettingLearningLanguagesForNonExistentUser() {
        // Given
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(UserNotFoundException.class, () -> {
            service.getLearningLanguages(userId);
        });
    }
}
