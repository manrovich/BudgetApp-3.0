package ru.manrovich.cashflow.domain.ledger.wallet;

import ru.manrovich.cashflow.domain.ledger.transaction.Money;

public record Wallet(
        WalletId id,
        Money amount,
        String name
) {

}
