package ru.manrovich.cashflow.api.wallet.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateWalletRequest(
        @NotBlank
        @Size(max = 255)
        String name,
        @NotNull
        @Size(min = 3, max = 3)
        String currencyCode
) {
}
