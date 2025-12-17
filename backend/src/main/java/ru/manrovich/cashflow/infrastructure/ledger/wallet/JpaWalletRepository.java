package ru.manrovich.cashflow.infrastructure.ledger.wallet;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface JpaWalletRepository extends JpaRepository<WalletEntity, UUID> {
}
