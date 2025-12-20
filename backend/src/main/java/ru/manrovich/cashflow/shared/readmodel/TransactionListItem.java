package ru.manrovich.cashflow.shared.readmodel;

import java.time.Instant;
import java.util.UUID;

public record TransactionListItem(
        UUID id,
        UUID walletId,
        String type,
        String amount,
        String currencyCode,
        Instant occurredAt,
        UUID categoryId
) {
}
