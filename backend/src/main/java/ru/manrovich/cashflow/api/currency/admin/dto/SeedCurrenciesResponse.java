package ru.manrovich.cashflow.api.currency.admin.dto;

import java.util.List;

public record SeedCurrenciesResponse(
        int inserted,
        int skipped,
        boolean dryRun,
        List<String> insertedCodes,
        String summary
) {
}
