package ru.manrovich.cashflow.domain.transaction.port;

import ru.manrovich.cashflow.domain.kernel.id.TransactionId;
import ru.manrovich.cashflow.domain.kernel.id.UserId;
import ru.manrovich.cashflow.domain.transaction.model.Transaction;

import java.util.Optional;

public interface TransactionRepository {

    Transaction save(Transaction transaction);

    Optional<Transaction> findById(UserId ownerId, TransactionId id);

    void deleteById(UserId ownerId, TransactionId id);
}
