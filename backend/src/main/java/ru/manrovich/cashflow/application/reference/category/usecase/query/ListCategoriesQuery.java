package ru.manrovich.cashflow.application.reference.category.usecase.query;

public record ListCategoriesQuery(
        String query,
        Integer page,
        Integer size
) {
}
