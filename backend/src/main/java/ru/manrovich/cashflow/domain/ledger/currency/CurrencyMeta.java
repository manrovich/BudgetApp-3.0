package ru.manrovich.cashflow.domain.ledger.currency;

public record CurrencyMeta(
        CurrencyCode code,
        String displayName,
        CurrencyKind kind,
        int scale
) {
}
