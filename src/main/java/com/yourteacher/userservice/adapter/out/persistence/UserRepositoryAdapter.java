package com.yourteacher.userservice.adapter.out.persistence;

import com.yourteacher.userservice.adapter.out.persistence.mapper.UserMapper;
import com.yourteacher.userservice.domain.model.User;
import com.yourteacher.userservice.domain.model.UserStatus;
import com.yourteacher.userservice.domain.port.out.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Adaptador de persistencia que implementa el puerto UserRepository
 * Conecta el dominio con la infraestructura de base de datos
 */
@Component
@RequiredArgsConstructor
public class UserRepositoryAdapter implements UserRepository {
    
    private final JpaUserRepository jpaRepository;
    private final UserMapper mapper;
    
    @Override
    public User save(User user) {
        var entity = mapper.toEntity(user);
        var savedEntity = jpaRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }
    
    @Override
    public Optional<User> findById(Long id) {
        return jpaRepository.findById(id)
                .map(mapper::toDomain);
    }
    
    @Override
    public Optional<User> findByEmail(String email) {
        return jpaRepository.findByEmail(email)
                .map(mapper::toDomain);
    }
    
    @Override
    public List<User> findAll() {
        return jpaRepository.findAll().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    public boolean existsByEmail(String email) {
        return jpaRepository.existsByEmail(email);
    }
    
    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }
    
    @Override
    public List<User> findActiveUsers() {
        return jpaRepository.findByStatus(UserStatus.ACTIVE).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
}
