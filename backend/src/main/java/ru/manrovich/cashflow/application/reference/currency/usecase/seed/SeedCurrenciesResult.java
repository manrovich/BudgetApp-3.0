package ru.manrovich.cashflow.application.reference.currency.usecase.seed;

import java.util.List;

public record SeedCurrenciesResult(
        int inserted,
        int skipped,
        boolean dryRun,
        List<String> insertedCodes
) {
}
