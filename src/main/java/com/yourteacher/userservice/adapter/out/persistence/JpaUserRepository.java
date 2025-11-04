package com.yourteacher.userservice.adapter.out.persistence;

import com.yourteacher.userservice.adapter.out.persistence.entity.UserEntity;
import com.yourteacher.userservice.domain.model.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio JPA para operaciones de base de datos
 * Spring Data JPA generará la implementación automáticamente
 */
@Repository
public interface JpaUserRepository extends JpaRepository<UserEntity, Long> {
    
    Optional<UserEntity> findByEmail(String email);
    
    boolean existsByEmail(String email);
    
    List<UserEntity> findByStatus(UserStatus status);
}
