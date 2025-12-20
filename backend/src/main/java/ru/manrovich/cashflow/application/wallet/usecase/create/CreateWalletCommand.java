package ru.manrovich.cashflow.application.wallet.usecase.create;

public record CreateWalletCommand(
        String name,
        String currencyCode
) {}
