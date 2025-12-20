package ru.manrovich.cashflow.application.reference.currency.usecase.seed;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.manrovich.cashflow.domain.kernel.id.CurrencyId;
import ru.manrovich.cashflow.domain.reference.currency.port.CurrencyQueryPort;
import ru.manrovich.cashflow.domain.reference.currency.port.CurrencyRepository;
import ru.manrovich.cashflow.testing.data.TestCurrencies;
import ru.manrovich.cashflow.testing.fake.InMemoryCurrencyStore;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SeedCurrenciesServiceTest {

    private InMemoryCurrencyStore store;
    private SeedCurrenciesService service;

    @BeforeEach
    void setUp() {
        store = new InMemoryCurrencyStore();
        service = new SeedCurrenciesService(store, store);
    }

    @Test
    void shouldInsertMissingCurrencies_andThenBeIdempotent() {
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
        SeedCurrenciesResult dry = service.execute(new SeedCurrenciesCommand(true));

        assertTrue(dry.inserted() > 0, "Dry run still computes what would be inserted");
        assertTrue(dry.dryRun());

        assertFalse(store.exists(new CurrencyId("USD")));
        assertTrue(store.isEmpty(), "Repository must stay empty after dryRun");
    }

    @Test
    void shouldSkipAlreadyExistingCurrency() {
        store.save(TestCurrencies.usd());

        SeedCurrenciesResult result = service.execute(new SeedCurrenciesCommand(false));
        int totalSeedCount = result.inserted() + result.skipped();

        assertTrue(totalSeedCount > 0);
        assertTrue(result.skipped() >= 1, "At least one currency should be skipped (USD already exists)");
        assertTrue(store.exists(new CurrencyId("USD")));
    }

    @Test
    void dryRun_shouldNotCallSaveAll() {
        CurrencyRepository repo = mock(CurrencyRepository.class);
        CurrencyQueryPort query = mock(CurrencyQueryPort.class);

        when(query.exists(any(CurrencyId.class))).thenReturn(false);

        SeedCurrenciesService service = new SeedCurrenciesService(repo, query);

        service.execute(new SeedCurrenciesCommand(true));

        verify(repo, never()).saveAll(any());
    }

    @Test
    void shouldNotCallSaveAll_whenNothingToInsert() {
        CurrencyRepository repo = mock(CurrencyRepository.class);
        CurrencyQueryPort query = mock(CurrencyQueryPort.class);

        // Пусть всё "уже существует" => toInsert пустой
        when(query.exists(any(CurrencyId.class))).thenReturn(true);

        SeedCurrenciesService service = new SeedCurrenciesService(repo, query);

        service.execute(new SeedCurrenciesCommand(false));

        verify(repo, never()).saveAll(any());
    }
}
