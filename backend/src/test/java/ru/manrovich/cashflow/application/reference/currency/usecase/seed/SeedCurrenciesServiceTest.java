package ru.manrovich.cashflow.application.reference.currency.usecase.seed;

import org.junit.jupiter.api.Test;
import ru.manrovich.cashflow.domain.kernel.id.CurrencyId;
import ru.manrovich.cashflow.domain.reference.currency.model.Currency;
import ru.manrovich.cashflow.domain.reference.currency.port.CurrencyQueryPort;
import ru.manrovich.cashflow.domain.reference.currency.port.CurrencyRepository;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.*;

class SeedCurrenciesServiceTest {

    @Test
    void shouldInsertMissingCurrencies_andThenBeIdempotent() {
        InMemoryCurrencyRepository repo = new InMemoryCurrencyRepository();
        InMemoryCurrencyQueryPort queryPort = new InMemoryCurrencyQueryPort(repo);

        SeedCurrenciesService service = new SeedCurrenciesService(repo, queryPort);

        SeedCurrenciesResult first = service.execute(new SeedCurrenciesCommand(false));
        int totalSeedCount = first.inserted() + first.skipped();

        assertTrue(totalSeedCount > 0, "Seed list must not be empty");
        assertTrue(first.inserted() > 0, "First run should insert something");
        assertEquals(totalSeedCount - first.inserted(), first.skipped());

        SeedCurrenciesResult second = service.execute(new SeedCurrenciesCommand(false));

        assertEquals(0, second.inserted(), "Second run must not insert anything (idempotent)");
        assertEquals(totalSeedCount, second.skipped(), "Second run must skip all seeded currencies");
        assertFalse(second.dryRun());
    }

    @Test
    void dryRun_shouldNotPersistAnything() {
        InMemoryCurrencyRepository repo = new InMemoryCurrencyRepository();
        InMemoryCurrencyQueryPort queryPort = new InMemoryCurrencyQueryPort(repo);

        SeedCurrenciesService service = new SeedCurrenciesService(repo, queryPort);

        SeedCurrenciesResult dry = service.execute(new SeedCurrenciesCommand(true));

        assertTrue(dry.inserted() > 0, "Dry run still computes what would be inserted");
        assertTrue(dry.dryRun());

        assertFalse(queryPort.exists(new CurrencyId("USD")));
        assertTrue(repo.isEmpty(), "Repository must stay empty after dryRun");
    }

    @Test
    void shouldSkipAlreadyExistingCurrency() {
        InMemoryCurrencyRepository repo = new InMemoryCurrencyRepository();
        InMemoryCurrencyQueryPort queryPort = new InMemoryCurrencyQueryPort(repo);

        // Предзаполним одну валюту из seed (USD почти наверняка есть)
        repo.save(new Currency(new CurrencyId("USD"), "US Dollar", 2, "$"));

        SeedCurrenciesService service = new SeedCurrenciesService(repo, queryPort);

        SeedCurrenciesResult result = service.execute(new SeedCurrenciesCommand(false));
        int totalSeedCount = result.inserted() + result.skipped();

        assertTrue(totalSeedCount > 0);
        assertTrue(result.skipped() >= 1, "At least one currency should be skipped (USD already exists)");
        assertTrue(queryPort.exists(new CurrencyId("USD")));
    }

    private static final class InMemoryCurrencyRepository implements CurrencyRepository {

        private final Map<String, Currency> storage = new ConcurrentHashMap<>();

        @Override
        public Currency save(Currency currency) {
            storage.put(currency.code().value(), currency);
            return currency;
        }

        @Override
        public void saveAll(Collection<Currency> currencies) {
            for (Currency c : currencies) {
                save(c);
            }
        }

        @Override
        public Optional<Currency> findByCode(CurrencyId code) {
            return Optional.ofNullable(storage.get(code.value()));
        }

        boolean isEmpty() {
            return storage.isEmpty();
        }
    }

    private static final class InMemoryCurrencyQueryPort implements CurrencyQueryPort {

        private final InMemoryCurrencyRepository repository;

        private InMemoryCurrencyQueryPort(InMemoryCurrencyRepository repository) {
            this.repository = repository;
        }

        @Override
        public boolean exists(CurrencyId code) {
            return repository.findByCode(code).isPresent();
        }
    }
}
