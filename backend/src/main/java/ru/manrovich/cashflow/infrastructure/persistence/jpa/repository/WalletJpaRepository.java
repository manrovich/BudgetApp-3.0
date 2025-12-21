package ru.manrovich.cashflow.infrastructure.persistence.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.manrovich.cashflow.infrastructure.persistence.jpa.entity.WalletEntity;

import java.util.Optional;
import java.util.UUID;

public interface WalletJpaRepository extends JpaRepository<WalletEntity, UUID> {

    boolean existsByIdAndOwnerId(UUID id, UUID ownerId);

    Optional<WalletEntity> findByIdAndOwnerId(UUID id, UUID ownerId);

    void deleteByIdAndOwnerId(UUID id, UUID ownerId);

    @Query("select w.currencyCode from WalletEntity w where w.id = :id and w.ownerId = :ownerId")
    Optional<String> findCurrencyCodeByIdAndOwnerId(@Param("ownerId") UUID ownerId,
                                                    @Param("id") UUID id);
}
