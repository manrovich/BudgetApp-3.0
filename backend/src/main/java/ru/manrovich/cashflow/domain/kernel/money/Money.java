package ru.manrovich.cashflow.domain.kernel.money;

import ru.manrovich.cashflow.domain.kernel.id.CurrencyId;
import ru.manrovich.cashflow.domain.kernel.validation.DomainPreconditions;

import java.math.BigDecimal;

public record Money(BigDecimal amount, CurrencyId currencyId) {

    public Money {
        DomainPreconditions.notNull(amount, "Amount must not be null");
        DomainPreconditions.notNull(currencyId, "CurrencyId must not be null");
    }

    public Money negate() {
        return new Money(amount.negate(), currencyId);
    }

    public Money add(Money other) {
        DomainPreconditions.notNull(other, "Other must not be null");
        DomainPreconditions.check(currencyId.equals(other.currencyId),
                "Currency mismatch: " + currencyId.value() + " vs " + other.currencyId.value());
        return new Money(amount.add(other.amount), currencyId);
    }

    public Money subtract(Money other) {
        DomainPreconditions.notNull(other, "Other must not be null");
        return add(other.negate());
    }

    public boolean isPositive() {
        return amount.compareTo(BigDecimal.ZERO) > 0;
    }

    public boolean isNegative() {
        return amount.compareTo(BigDecimal.ZERO) < 0;
    }

    public Money abs() {
        return isNegative() ? negate() : this;
    }
}