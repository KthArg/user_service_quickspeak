package com.yourteacher.userservice.adapter.out.persistence;

import com.yourteacher.userservice.adapter.out.persistence.mapper.LanguageMapper;
import com.yourteacher.userservice.domain.model.Language;
import com.yourteacher.userservice.domain.port.out.LanguageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Adaptador de persistencia que implementa el puerto LanguageRepository
 * Conecta el dominio con la infraestructura de base de datos
 */
@Component
@RequiredArgsConstructor
public class LanguageRepositoryAdapter implements LanguageRepository {

    private final JpaLanguageRepository jpaRepository;
    private final LanguageMapper mapper;

    @Override
    public Language save(Language language) {
        var entity = mapper.toEntity(language);
        var savedEntity = jpaRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public List<Language> findAll() {
        return jpaRepository.findAll().stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public Optional<Language> findById(Long id) {
        return jpaRepository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public Optional<Language> findByCode(String code) {
        return jpaRepository.findByCodeIgnoreCase(code)
                .map(mapper::toDomain);
    }

    @Override
    public Optional<Language> findByName(String name) {
        return jpaRepository.findByNameIgnoreCase(name)
                .map(mapper::toDomain);
    }

    @Override
    public List<Language> searchByName(String searchTerm) {
        return jpaRepository.searchByName(searchTerm).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<Language> findStartingLanguages() {
        // Obtenemos los primeros 10 idiomas
        var entities = jpaRepository.findTop10ByOrderByNameAsc();
        return entities.stream()
                .limit(10)
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public boolean existsByCode(String code) {
        return jpaRepository.existsByCodeIgnoreCase(code);
    }

    @Override
    public boolean existsById(Long id) {
        return jpaRepository.existsById(id);
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public long count() {
        return jpaRepository.count();
    }
}
