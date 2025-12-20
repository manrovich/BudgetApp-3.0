package ru.manrovich.cashflow.application.transaction.web.create;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.UUID;

import java.math.BigDecimal;
import java.time.Instant;

public record CreateTransactionRequest(
        @NotNull
        @UUID
        String walletId,

        @Nullable
        @UUID
        String categoryId,

        @NotNull
        BigDecimal amount,

        @NotNull
        Instant occurredAt
) {}
