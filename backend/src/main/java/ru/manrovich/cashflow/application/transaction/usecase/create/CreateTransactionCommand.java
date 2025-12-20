package ru.manrovich.cashflow.application.transaction.usecase.create;

import java.math.BigDecimal;
import java.time.Instant;

public record CreateTransactionCommand(
        String walletId,
        String categoryId,
        BigDecimal amount,
        Instant occurredAt
) {}
