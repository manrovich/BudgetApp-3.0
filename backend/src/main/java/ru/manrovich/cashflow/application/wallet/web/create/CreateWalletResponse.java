package ru.manrovich.cashflow.application.wallet.web.create;

public record CreateWalletResponse(
        String id,
        String name,
        String currencyCode
) {}
