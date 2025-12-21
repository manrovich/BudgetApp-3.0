package ru.manrovich.cashflow.shared.query;

public record SlicePage(
        int number,
        int size,
        boolean hasNext
) {}
