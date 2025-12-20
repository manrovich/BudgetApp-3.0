package ru.manrovich.cashflow.infrastructure.persistence.jpa.repository.projection;

import java.util.UUID;

public interface WalletListRow {
    UUID getId();
    String getName();
    String getCurrencyCode();
}
