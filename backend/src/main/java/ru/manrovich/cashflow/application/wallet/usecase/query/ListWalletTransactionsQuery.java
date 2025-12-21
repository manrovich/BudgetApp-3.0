package ru.manrovich.cashflow.application.wallet.usecase.query;

import java.time.Instant;

public record ListWalletTransactionsQuery(
        String walletId,
        Instant from,
        Instant to
) {
}