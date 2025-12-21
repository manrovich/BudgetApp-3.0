package ru.manrovich.cashflow.infrastructure.persistence.jpa.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.manrovich.cashflow.infrastructure.persistence.jpa.entity.CategoryEntity;
import ru.manrovich.cashflow.infrastructure.persistence.jpa.repository.projection.CategoryListRow;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CategoryJpaRepository extends JpaRepository<CategoryEntity, UUID> {
    boolean existsByUserIdAndNameIgnoreCase(UUID userId, String name);

    boolean existsByUserIdAndId(UUID userId, UUID id);

    Optional<CategoryEntity> findByUserIdAndId(UUID userId, UUID id);

    void deleteByUserIdAndId(UUID userId, UUID id);

    @Query("""
           select
             c.id as id,
             c.name as name
           from CategoryEntity c
           where c.userId = :ownerId
             and (:query is null or :query = '' or lower(c.name) like lower(concat('%', :query, '%')))
           """)
    List<CategoryListRow> findListRows(UUID ownerId, String query, Pageable pageable);
}
