package ru.manrovich.cashflow.application.reference.currency.web.dto;

public record SeedCurrenciesRequest(Boolean dryRun) {

    public boolean dryRunOrDefault() {
        return dryRun != null && dryRun;
    }

    public static SeedCurrenciesRequest defaultRequest() {
        return new SeedCurrenciesRequest(false);
    }
}
