package com.yourteacher.userservice.adapter.out.persistence;

import com.yourteacher.userservice.adapter.out.persistence.mapper.UserLanguageMapper;
import com.yourteacher.userservice.domain.model.UserLanguage;
import com.yourteacher.userservice.domain.port.out.UserLanguageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Adaptador de persistencia que implementa el puerto UserLanguageRepository
 * Conecta el dominio con la infraestructura de base de datos
 */
@Component
@RequiredArgsConstructor
public class UserLanguageRepositoryAdapter implements UserLanguageRepository {

    private final JpaUserLanguageRepository jpaRepository;
    private final UserLanguageMapper mapper;

    @Override
    @Transactional
    public UserLanguage save(UserLanguage userLanguage) {
        var entity = mapper.toEntity(userLanguage);
        var savedEntity = jpaRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<UserLanguage> findById(Long id) {
        return jpaRepository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public List<UserLanguage> findByUserId(Long userId) {
        return jpaRepository.findByUserId(userId).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public Optional<UserLanguage> findByUserIdAndLanguageId(Long userId, Long languageId) {
        return jpaRepository.findByUserIdAndLanguageId(userId, languageId)
                .map(mapper::toDomain);
    }

    @Override
    public Optional<UserLanguage> findNativeLanguageByUserId(Long userId) {
        return jpaRepository.findByUserIdAndIsNativeTrue(userId)
                .map(mapper::toDomain);
    }

    @Override
    public List<UserLanguage> findLearningLanguagesByUserId(Long userId) {
        return jpaRepository.findByUserIdAndIsNativeFalse(userId).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<UserLanguage> findByLanguageId(Long languageId) {
        return jpaRepository.findByLanguageId(languageId).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public boolean existsByUserIdAndLanguageId(Long userId, Long languageId) {
        return jpaRepository.existsByUserIdAndLanguageId(userId, languageId);
    }

    @Override
    public boolean hasNativeLanguage(Long userId) {
        return jpaRepository.hasNativeLanguage(userId);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void deleteByUserIdAndLanguageId(Long userId, Long languageId) {
        jpaRepository.deleteByUserIdAndLanguageId(userId, languageId);
    }

    @Override
    @Transactional
    public void deleteAllByUserId(Long userId) {
        jpaRepository.deleteByUserId(userId);
    }

    @Override
    public long countByUserId(Long userId) {
        return jpaRepository.countByUserId(userId);
    }

    @Override
    public long countByLanguageId(Long languageId) {
        return jpaRepository.countByLanguageId(languageId);
    }
}
