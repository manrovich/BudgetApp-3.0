package ru.manrovich.cashflow.domain.reference.category.model;

import ru.manrovich.cashflow.domain.kernel.validation.DomainPreconditions;

public record CategoryName(String value) {

    private static final int MAX_LENGTH = 128;

    public CategoryName {
        DomainPreconditions.notBlank(value, "Category name must not be blank");
        value = value.trim();
        DomainPreconditions.check(value.length() <= MAX_LENGTH, "Category name length must be <= " + MAX_LENGTH);
    }
}
