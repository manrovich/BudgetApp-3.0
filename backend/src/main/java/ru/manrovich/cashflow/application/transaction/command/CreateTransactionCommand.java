package ru.manrovich.cashflow.application.transaction.command;

import java.math.BigDecimal;
import java.time.Instant;

public record CreateTransactionCommand(
        String walletId,
        String categoryId,
        String type,
        BigDecimal amount,
        Instant occurredAt
) {
}
