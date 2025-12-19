package ru.manrovich.cashflow.domain.wallet.port;

import ru.manrovich.cashflow.domain.kernel.id.CurrencyId;
import ru.manrovich.cashflow.domain.kernel.id.UserId;
import ru.manrovich.cashflow.domain.kernel.id.WalletId;

public interface WalletQueryPort {

    boolean exists(UserId ownerId, WalletId walletId);

    CurrencyId getCurrencyId(UserId ownerId, WalletId walletId);
}
