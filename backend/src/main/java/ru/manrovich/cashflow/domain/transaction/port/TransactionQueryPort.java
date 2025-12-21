package ru.manrovich.cashflow.domain.transaction.port;

import ru.manrovich.cashflow.domain.kernel.id.TransactionId;
import ru.manrovich.cashflow.domain.kernel.id.UserId;
import ru.manrovich.cashflow.domain.kernel.id.WalletId;
import ru.manrovich.cashflow.domain.transaction.port.filter.TransactionFilter;
import ru.manrovich.cashflow.shared.readmodel.TransactionListItem;

import java.math.BigDecimal;
import java.util.List;

public interface TransactionQueryPort {

    boolean exists(UserId ownerId, TransactionId id);

    boolean existsByWalletId(UserId ownerId, WalletId walletId);

    BigDecimal sumAmountsByWalletId(UserId ownerId, WalletId walletId);

    List<TransactionListItem> findListItems(TransactionFilter filter);
}
