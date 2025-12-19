package ru.manrovich.cashflow.domain.wallet.port;

import ru.manrovich.cashflow.domain.kernel.id.UserId;
import ru.manrovich.cashflow.domain.kernel.id.WalletId;
import ru.manrovich.cashflow.domain.wallet.model.Wallet;

import java.util.Optional;

public interface WalletRepository {

    Wallet save(Wallet wallet);

    Optional<Wallet> findById(UserId ownerId, WalletId walletId);

    void deleteById(UserId ownerId, WalletId walletId);
}
