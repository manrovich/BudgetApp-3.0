package ru.manrovich.cashflow.domain.ledger.currency;

public interface CurrencyRegistry {
    CurrencyMeta getMeta(CurrencyCode code);
}
