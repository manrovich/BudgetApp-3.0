package ru.manrovich.cashflow.infrastructure.persistence.jpa.repository.projection;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public interface TransactionListRow {
    UUID getId();
    UUID getWalletId();
    String getType();
    BigDecimal getAmount();
    String getCurrencyCode();
    Instant getOccurredAt();
    UUID getCategoryId();
}
