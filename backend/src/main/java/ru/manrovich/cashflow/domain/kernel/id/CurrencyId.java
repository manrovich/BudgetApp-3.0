package ru.manrovich.cashflow.domain.kernel.id;

import ru.manrovich.cashflow.domain.kernel.validation.DomainPreconditions;

import java.util.Locale;

public record CurrencyId(String value) {

    public CurrencyId {
        String normalized = DomainPreconditions.notBlank(value, "CurrencyId must not be blank").trim();
        normalized = normalized.toUpperCase(Locale.ROOT);
        value = normalized;
    }
}
