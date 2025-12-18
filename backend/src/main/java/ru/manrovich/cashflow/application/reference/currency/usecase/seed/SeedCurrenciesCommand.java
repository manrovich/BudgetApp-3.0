package ru.manrovich.cashflow.application.reference.currency.usecase.seed;

public record SeedCurrenciesCommand(
        boolean dryRun
) {
    public static SeedCurrenciesCommand defaultCommand() {
        return new SeedCurrenciesCommand(false);
    }
}
