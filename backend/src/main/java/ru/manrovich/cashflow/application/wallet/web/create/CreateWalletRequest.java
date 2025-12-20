package ru.manrovich.cashflow.application.wallet.web.create;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreateWalletRequest(
        @NotBlank
        @Size(max = 64)
        String name,

        @Schema(example = "RUB", description = "ISO 4217 code")
        @NotBlank
        @Pattern(regexp = "^[A-Z]{3}$")
        String currencyCode
) {}
