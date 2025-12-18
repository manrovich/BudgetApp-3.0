package ru.manrovich.cashflow.domain.reference.currency.model;

import ru.manrovich.cashflow.domain.kernel.id.CurrencyId;
import ru.manrovich.cashflow.domain.kernel.validation.DomainPreconditions;

public record Currency(
        CurrencyId code,
        String name,
        int scale,
        String symbol
) {

    private static final int MIN_SCALE = 0;
    private static final int MAX_SCALE = 4;

    public Currency {
        DomainPreconditions.notNull(code, "Currency value must not be null");
        DomainPreconditions.notBlank(name, "Currency name must not be blank");

        // FIAT-only MVP: ISO 4217 — 3 буквы
        DomainPreconditions.check(
                code.value().matches("^[A-Z]{3}$"),
                "Currency value must be ISO-4217 (3 letters), e.g. RUB, USD"
        );
        DomainPreconditions.check(scale >= MIN_SCALE && scale <= MAX_SCALE,
                "Currency scale must be between 0 and 18");

        if (symbol != null) {
            DomainPreconditions.notBlank(symbol, "Currency symbol must not be blank when provided");
        }
    }
}
