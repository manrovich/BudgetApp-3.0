package ru.manrovich.cashflow.api.transaction.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
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
        @Schema(example = "INCOME", description = "INCOME or EXPENSE")
        @NotNull
        @Pattern(regexp = "INCOME|EXPENSE")
        String type,
        @NotNull
        BigDecimal amount,
        @NotNull
        Instant occurredAt
) {
}
