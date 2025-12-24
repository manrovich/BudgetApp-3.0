package ru.manrovich.cashflow.application.currency.command;

import java.util.List;

public record SeedCurrenciesResult(
        int inserted,
        int skipped,
        boolean dryRun,
        List<String> insertedCodes
) {
}
