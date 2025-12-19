package ru.manrovich.cashflow.application.transaction.usecase.create;

import java.time.Instant;

public record CreateTransactionCommand(
        String walletId,
        String categoryId,
        String amount,
        Instant occurredAt
) {}
