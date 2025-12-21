package ru.manrovich.cashflow.application.reference.currency.usecase.query;

public record ListCurrenciesQuery(
        String query,
        Integer page,
        Integer size
) {
}
