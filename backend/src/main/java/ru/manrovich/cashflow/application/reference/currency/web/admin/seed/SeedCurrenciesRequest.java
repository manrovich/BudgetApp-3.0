package ru.manrovich.cashflow.application.reference.currency.web.admin.seed;

import jakarta.validation.constraints.NotNull;

public record SeedCurrenciesRequest(
        @NotNull
        Boolean dryRun
) {
    public static SeedCurrenciesRequest defaultRequest() {
        return new SeedCurrenciesRequest(false);
    }
}
