package ru.manrovich.cashflow.shared.readmodel;

import java.util.UUID;

public record WalletListItem(
        UUID id,
        String name,
        String currencyCode
) {}
