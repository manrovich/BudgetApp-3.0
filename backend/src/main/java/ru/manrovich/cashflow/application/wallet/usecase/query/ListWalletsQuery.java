package ru.manrovich.cashflow.application.wallet.usecase.query;

public record ListWalletsQuery(
        Integer page,
        Integer size
) {}
