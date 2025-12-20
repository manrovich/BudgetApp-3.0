package ru.manrovich.cashflow.application.reference.currency.web.admin.seed;

public record SeedCurrenciesRequest(
        boolean dryRun
) {
    public static SeedCurrenciesRequest defaultRequest() {
        return new SeedCurrenciesRequest(false);
    }
}
