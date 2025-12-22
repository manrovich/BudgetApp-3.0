package ru.manrovich.cashflow.infrastructure.persistence.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.manrovich.cashflow.infrastructure.persistence.jpa.entity.TransactionEntity;
import ru.manrovich.cashflow.infrastructure.persistence.jpa.repository.projection.TransactionListRow;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TransactionJpaRepository extends JpaRepository<TransactionEntity, UUID> {

    Optional<TransactionEntity> findByIdAndOwnerId(UUID id, UUID ownerId);

    boolean existsByOwnerIdAndId(UUID ownerId, UUID id);

    void deleteByIdAndOwnerId(UUID id, UUID ownerId);

    boolean existsByOwnerIdAndWalletId(UUID ownerId, UUID walletId);

    @Query("""
        select
        coalesce(
            sum(
                case
                    when t.type = 'EXPENSE'
                    then -t.amount
                    else t.amount
                end),
        0)
        from TransactionEntity t
        where t.ownerId = :ownerId and t.walletId = :walletId
    """)
    BigDecimal sumAmountsByOwnerIdAndWalletId(UUID ownerId, UUID walletId);

    @Query("""
    select
      t.id as id,
      t.walletId as walletId,
      t.type as type,
      t.amount as amount,
      t.currencyCode as currencyCode,
      t.occurredAt as occurredAt,
      t.categoryId as categoryId
    from TransactionEntity t
    where t.ownerId = :ownerId
      and (:walletId is null or t.walletId = :walletId)
      and (:from is null or t.occurredAt >= :from)
      and (:to is null or t.occurredAt <= :to)
    order by t.occurredAt asc
    """)
    List<TransactionListRow> findListRows(
           UUID ownerId,
           UUID walletId,
           Instant from,
           Instant to
    );
}
