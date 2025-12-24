package ru.manrovich.cashflow.domain.wallet.port;

import ru.manrovich.cashflow.domain.kernel.id.CurrencyId;

public record WalletSnapshot(CurrencyId currencyId) {
}
