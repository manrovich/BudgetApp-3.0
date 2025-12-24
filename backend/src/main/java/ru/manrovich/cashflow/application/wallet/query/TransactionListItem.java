package ru.manrovich.cashflow.application.wallet.query;

import java.time.Instant;

public record TransactionListItem(
        String id,
        String walletId,
        String type,
        String amount,
        String currencyCode,
        Instant occurredAt,
        String categoryId
) {
}
