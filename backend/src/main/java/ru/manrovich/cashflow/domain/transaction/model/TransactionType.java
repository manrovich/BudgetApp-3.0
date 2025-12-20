package ru.manrovich.cashflow.domain.transaction.model;

import ru.manrovich.cashflow.domain.kernel.exception.ValidationException;
import ru.manrovich.cashflow.domain.kernel.validation.DomainPreconditions;

public enum TransactionType {
    INCOME,
    EXPENSE;

    public static TransactionType parse(String raw) {
        raw = DomainPreconditions.notBlank(raw, "Transaction type must not be blank");

        try {
            return TransactionType.valueOf(raw);
        } catch (IllegalArgumentException ex) {
            throw new ValidationException("Unknown transaction type: " + raw, ex);
        }
    }
}
