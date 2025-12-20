package ru.manrovich.cashflow.domain.kernel.money;

import ru.manrovich.cashflow.domain.kernel.id.CurrencyId;
import ru.manrovich.cashflow.domain.kernel.validation.DomainPreconditions;

import java.math.BigDecimal;

public record Money(BigDecimal amount, CurrencyId currencyId) {

    public Money {
        DomainPreconditions.notNull(amount, "Amount must not be null");
        DomainPreconditions.check(amount.compareTo(BigDecimal.ZERO) != 0, "Amount must be != 0");
        DomainPreconditions.notNull(currencyId, "CurrencyId must not be null");
    }
}
