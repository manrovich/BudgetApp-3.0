package ru.manrovich.cashflow.application.transaction.query;

import ru.manrovich.cashflow.application.wallet.query.TransactionListItem;
import ru.manrovich.cashflow.domain.kernel.id.UserId;
import ru.manrovich.cashflow.domain.kernel.id.WalletId;

import java.time.Instant;
import java.util.List;

public interface TransactionReadRepository {
    List<TransactionListItem> findWalletTransactions(UserId ownerId, WalletId walletId, Instant from, Instant to);
}
