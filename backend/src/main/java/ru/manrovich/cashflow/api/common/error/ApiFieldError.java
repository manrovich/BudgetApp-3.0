package ru.manrovich.cashflow.api.common.error;

public record ApiFieldError(
        String field,
        String message
) {}
