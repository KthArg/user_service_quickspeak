package com.yourteacher.userservice.adapter.out.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entidad JPA para persistencia de relaciones usuario-idioma
 * Adapter Layer - Infrastructure
 */
@Entity
@Table(name = "user_languages",
       uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "language_id"}))
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserLanguageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "language_id", nullable = false)
    private Long languageId;

    @Column(name = "is_native", nullable = false)
    private boolean isNative;

    @Column(name = "added_at", nullable = false, updatable = false)
    private LocalDateTime addedAt;

    @PrePersist
    protected void onCreate() {
        if (addedAt == null) {
            addedAt = LocalDateTime.now();
        }
    }
}
