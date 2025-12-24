package ru.manrovich.cashflow.api.wallet.dto;

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
