package ru.manrovich.cashflow.domain.kernel.id;

import ru.manrovich.cashflow.domain.kernel.validation.DomainPreconditions;

import java.util.UUID;

public record TransactionId(UUID value) {
    public TransactionId {
        DomainPreconditions.notNull(value, "TransactionId must not be null");
    }
}
