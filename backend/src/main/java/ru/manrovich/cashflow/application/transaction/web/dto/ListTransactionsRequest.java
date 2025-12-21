package ru.manrovich.cashflow.application.transaction.web.dto;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.hibernate.validator.constraints.UUID;

import java.time.Instant;

public record ListTransactionsRequest(
        @Nullable
        @UUID
        String walletId,
        @Nullable
        Instant from,
        @Nullable
        Instant to,
        @Nullable
        @Min(0)
        Integer page,
        @Nullable
        @Min(1)
        @Max(200)
        Integer size
) {}
