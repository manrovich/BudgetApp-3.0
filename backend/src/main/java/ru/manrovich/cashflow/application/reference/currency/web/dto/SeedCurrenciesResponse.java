package ru.manrovich.cashflow.application.reference.currency.web.dto;

import java.util.List;

public record SeedCurrenciesResponse(
        int inserted,
        int skipped,
        boolean dryRun,
        List<String> insertedCodes,
        String summary
) {
}
