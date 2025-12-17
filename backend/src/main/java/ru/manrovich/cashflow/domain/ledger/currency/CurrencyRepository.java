package ru.manrovich.cashflow.domain.ledger.currency;

import java.util.Optional;

public interface CurrencyRepository {
    Optional<CurrencyMeta> findByCode(CurrencyCode code);
}
