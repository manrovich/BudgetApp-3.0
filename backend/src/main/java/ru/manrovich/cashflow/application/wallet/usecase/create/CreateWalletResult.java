package ru.manrovich.cashflow.application.wallet.usecase.create;

public record CreateWalletResult(
        String id,
        String name,
        String currencyCode
) {}
