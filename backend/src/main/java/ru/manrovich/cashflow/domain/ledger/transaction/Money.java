package ru.manrovich.cashflow.domain.ledger.transaction;

import ru.manrovich.cashflow.domain.ledger.currency.CurrencyCode;
import ru.manrovich.cashflow.domain.ledger.currency.CurrencyMeta;

import java.math.BigDecimal;
import java.math.RoundingMode;

public record Money(BigDecimal amount, CurrencyCode currency) {

    public Money {
        if (amount == null) {
            throw new IllegalArgumentException("Amount must not be null");
        }
        if (currency == null) {
            throw new IllegalArgumentException("Currency must not be null");
        }
    }

    public static Money of(BigDecimal rawAmount, CurrencyMeta meta) {
        if (rawAmount == null) {
            throw new IllegalArgumentException("Amount must not be null");
        }
        BigDecimal normalized = rawAmount.setScale(meta.scale(), RoundingMode.HALF_UP);
        return new Money(normalized, meta.code());
    }

//    public Money add(Money other) {
//        requireSameCurrency(other);
//        return new Money(this.amount.add(other.amount), this.currency);
//    }
//
//    public Money subtract(Money other) {
//        requireSameCurrency(other);
//        return new Money(this.amount.subtract(other.amount), this.currency);
//    }

    private void requireSameCurrency(Money other) {
        if (!this.currency.equals(other.currency)) {
            throw new IllegalArgumentException(
                    "Cannot operate on money with different currencies: " +
                            this.currency + " vs " + other.currency
            );
        }
    }
}

