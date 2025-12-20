package ru.manrovich.cashflow.domain.reference.currency.port;

import ru.manrovich.cashflow.domain.kernel.id.CurrencyId;
import ru.manrovich.cashflow.domain.reference.currency.model.Currency;

import java.util.Collection;
import java.util.Optional;

public interface CurrencyRepository {

    Currency save(Currency currency);

    void saveAll(Collection<Currency> currencies);

    Optional<Currency> findByCode(CurrencyId code);
}
