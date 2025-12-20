package ru.manrovich.cashflow.domain.transaction.port;

import ru.manrovich.cashflow.domain.kernel.id.TransactionId;
import ru.manrovich.cashflow.domain.kernel.id.UserId;
import ru.manrovich.cashflow.domain.kernel.id.WalletId;
import ru.manrovich.cashflow.shared.query.Slice;
import ru.manrovich.cashflow.shared.readmodel.TransactionListItem;

import java.math.BigDecimal;
import java.time.Instant;

public interface TransactionQueryPort {

    boolean exists(UserId ownerId, TransactionId id);

    boolean existsByWalletId(UserId ownerId, WalletId walletId);

    BigDecimal sumAmountsByWalletId(UserId ownerId, WalletId walletId);

    Slice<TransactionListItem> findListItems(TransactionSearchCriteria criteria);

    record TransactionSearchCriteria(
            WalletId walletId,
            Instant from,
            Instant to,
            int page,
            int size
    ) {}
}
