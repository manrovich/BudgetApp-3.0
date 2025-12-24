package ru.manrovich.cashflow.domain.wallet.port;

import ru.manrovich.cashflow.domain.kernel.id.UserId;
import ru.manrovich.cashflow.domain.kernel.id.WalletId;

import java.util.Optional;

public interface WalletQueryPort {

    Optional<WalletSnapshot> findSnapshot(UserId ownerId, WalletId walletId);
}
