package ru.manrovich.cashflow.domain.ledger.transaction;

import ru.manrovich.cashflow.domain.ledger.wallet.WalletId;

import java.util.List;
import java.util.Optional;

public interface TransactionRepository {

    Optional<Transaction> findById(TransactionId id);

    List<Transaction> findByWalletId(WalletId walletId);

    Transaction save(Transaction transaction);

    void deleteById(TransactionId id);
}