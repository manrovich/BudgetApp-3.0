package ru.manrovich.cashflow.domain.kernel.id;

import ru.manrovich.cashflow.domain.kernel.validation.DomainPreconditions;

import java.util.UUID;

public record UserId(UUID value) {
    public UserId {
        DomainPreconditions.notNull(value, "UserId must not be null");
    }
}
