package ru.manrovich.cashflow.domain.kernel.id;

import ru.manrovich.cashflow.domain.kernel.validation.DomainPreconditions;

import java.util.UUID;

public record WalletId(UUID value) {

    public WalletId {
        DomainPreconditions.notNull(value, "WalletId must not be null");
    }
}
