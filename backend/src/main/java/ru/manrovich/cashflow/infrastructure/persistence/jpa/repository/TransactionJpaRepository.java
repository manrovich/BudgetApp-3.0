package ru.manrovich.cashflow.infrastructure.persistence.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.manrovich.cashflow.infrastructure.persistence.jpa.entity.TransactionEntity;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

public interface TransactionJpaRepository extends JpaRepository<TransactionEntity, UUID> {

    Optional<TransactionEntity> findByIdAndOwnerId(UUID id, UUID ownerId);

    boolean existsByOwnerIdAndId(UUID ownerId, UUID id);

    void deleteByIdAndOwnerId(UUID id, UUID ownerId);

    boolean existsByOwnerIdAndWalletId(UUID ownerId, UUID walletId);

    @Query("""
            select coalesce(sum(t.amount), 0)
            from TransactionEntity t
            where t.ownerId = :ownerId and t.walletId = :walletId
           """)
    BigDecimal sumAmountsByOwnerIdAndWalletId(UUID ownerId, UUID walletId);
}
