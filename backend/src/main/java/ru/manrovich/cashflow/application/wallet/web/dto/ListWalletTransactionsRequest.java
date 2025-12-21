package ru.manrovich.cashflow.application.wallet.web.dto;

import jakarta.validation.constraints.NotNull;

import java.time.Instant;

public record ListWalletTransactionsRequest(
        @NotNull
        Instant from,
        @NotNull
        Instant to
) {
}
