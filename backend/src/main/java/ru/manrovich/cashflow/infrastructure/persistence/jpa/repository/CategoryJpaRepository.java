package ru.manrovich.cashflow.infrastructure.persistence.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.manrovich.cashflow.infrastructure.persistence.jpa.entity.CategoryEntity;

import java.util.Optional;
import java.util.UUID;

public interface CategoryJpaRepository extends JpaRepository<CategoryEntity, UUID> {
    boolean existsByUserIdAndNameIgnoreCase(UUID userId, String name);

    boolean existsByUserIdAndId(UUID userId, UUID id);

    Optional<CategoryEntity> findByUserIdAndId(UUID userId, UUID id);

    void deleteByUserIdAndId(UUID userId, UUID id);
}
