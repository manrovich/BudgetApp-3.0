package ru.manrovich.cashflow.application.common.web.dto;

public record ApiFieldError(
        String field,
        String message
) {}
