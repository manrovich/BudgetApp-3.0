package ru.manrovich.cashflow.shared.readmodel;

public record CurrencyListItem(
        String code,
        String name,
        int scale,
        String symbol
) {
}
