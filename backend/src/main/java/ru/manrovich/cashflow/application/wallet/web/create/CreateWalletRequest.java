package ru.manrovich.cashflow.application.wallet.web.create;

public record CreateWalletRequest(
        String name,
        String currencyCode
) {}
