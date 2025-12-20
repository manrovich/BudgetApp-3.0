package ru.manrovich.cashflow.application.transaction.web.create;

import org.springframework.lang.Nullable;

import java.time.Instant;

public record CreateTransactionRequest(
        String walletId,
        @Nullable String categoryId,
        String amount,
        Instant occurredAt
) {}
