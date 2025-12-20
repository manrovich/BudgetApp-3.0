package ru.manrovich.cashflow.domain.kernel.id;

import ru.manrovich.cashflow.domain.kernel.validation.DomainPreconditions;

import java.util.UUID;

public record CategoryId(UUID value) {
    public CategoryId {
        DomainPreconditions.notNull(value, "CategoryId must not be null");
    }
}
