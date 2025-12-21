package ru.manrovich.cashflow.testing.fake;

import ru.manrovich.cashflow.domain.kernel.id.CurrencyId;
import ru.manrovich.cashflow.domain.reference.currency.model.Currency;
import ru.manrovich.cashflow.domain.reference.currency.port.CurrencyQueryPort;
import ru.manrovich.cashflow.domain.reference.currency.port.CurrencyRepository;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public final class InMemoryCurrencyStore implements CurrencyRepository, CurrencyQueryPort {
    private final Map<String, Currency> storage = new ConcurrentHashMap<>();

    @Override
    public Currency save(Currency currency) {
        storage.put(currency.code().value(), currency);
        return currency;
    }

    @Override
    public void saveAll(Collection<Currency> currencies) {
        currencies.forEach(this::save);
    }

    @Override
    public Optional<Currency> findByCode(CurrencyId code) {
        return Optional.ofNullable(storage.get(code.value()));
    }

    @Override
    public boolean exists(CurrencyId code) {
        return findByCode(code).isPresent();
    }

    public boolean isEmpty() {
        return storage.isEmpty();
    }
}
