package ru.manrovich.cashflow.domain.ledger.wallet;

import java.util.Optional;

public interface WalletRepository {
    Optional<Wallet> findById(WalletId id);
}
