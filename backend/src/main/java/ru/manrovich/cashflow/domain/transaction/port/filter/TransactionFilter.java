package ru.manrovich.cashflow.domain.transaction.port.filter;

import ru.manrovich.cashflow.domain.kernel.exception.ValidationException;
import ru.manrovich.cashflow.domain.kernel.id.UserId;
import ru.manrovich.cashflow.domain.kernel.id.WalletId;
import ru.manrovich.cashflow.domain.kernel.validation.DomainPreconditions;

import java.time.Instant;

public record TransactionFilter(
        UserId ownerId,
        WalletId walletId, // TODO WalletScope (ALL, ONE, MANY)
        Instant from,
        Instant to
) {
    public TransactionFilter {
        DomainPreconditions.notNull(ownerId, "ownerId must not be null");

        if (from != null && to != null && from.isAfter(to)) {
            throw new ValidationException("'from' must be <= 'to'");
        }
    }
}