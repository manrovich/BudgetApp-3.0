package ru.manrovich.cashflow.domain.transaction.port;

import ru.manrovich.cashflow.domain.kernel.id.TransactionId;
import ru.manrovich.cashflow.domain.kernel.id.UserId;
import ru.manrovich.cashflow.domain.kernel.id.WalletId;

import java.math.BigDecimal;

public interface TransactionQueryPort {

    boolean exists(UserId ownerId, TransactionId id);

    boolean existsByWalletId(UserId ownerId, WalletId walletId);

    BigDecimal sumAmountsByWalletId(UserId ownerId, WalletId walletId);
}
