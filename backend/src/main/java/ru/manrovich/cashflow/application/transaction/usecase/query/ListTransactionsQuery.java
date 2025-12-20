package ru.manrovich.cashflow.application.transaction.usecase.query;

import java.time.Instant;

public record ListTransactionsQuery(
        String walletId,
        Instant from,
        Instant to,
        Integer page,
        Integer size
) {}