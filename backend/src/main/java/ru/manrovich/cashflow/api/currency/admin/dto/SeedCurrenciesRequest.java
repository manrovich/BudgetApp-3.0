package ru.manrovich.cashflow.api.currency.admin.dto;

public record SeedCurrenciesRequest(Boolean dryRun) {

    public boolean dryRunOrDefault() {
        return dryRun != null && dryRun;
    }

    public static SeedCurrenciesRequest defaultRequest() {
        return new SeedCurrenciesRequest(false);
    }
}
