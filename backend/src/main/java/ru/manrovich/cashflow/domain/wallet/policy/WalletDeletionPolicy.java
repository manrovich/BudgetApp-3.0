package ru.manrovich.cashflow.domain.wallet.policy;

import ru.manrovich.cashflow.domain.kernel.id.WalletId;
import ru.manrovich.cashflow.domain.kernel.validation.DomainPreconditions;

/**
 * Политика удаления кошелька
 */
public class WalletDeletionPolicy {

    public void assertCanDelete(WalletId walletId) {
        DomainPreconditions.notNull(walletId, "WalletId must not be null");
        // TODO: после появления транзакций:
        // - нельзя удалять, если есть транзакции
        // - нельзя удалять при non-zero балансе
    }
}
