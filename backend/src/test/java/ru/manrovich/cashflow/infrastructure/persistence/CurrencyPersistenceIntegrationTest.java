package ru.manrovich.cashflow.infrastructure.persistence;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import ru.manrovich.cashflow.domain.kernel.id.CurrencyId;
import ru.manrovich.cashflow.domain.reference.currency.model.Currency;
import ru.manrovich.cashflow.infrastructure.persistence.jpa.adapter.CurrencyQueryPortAdapter;
import ru.manrovich.cashflow.infrastructure.persistence.jpa.adapter.CurrencyRepositoryAdapter;
import ru.manrovich.cashflow.infrastructure.persistence.jpa.mapper.CurrencyEntityMapper;
import ru.manrovich.cashflow.infrastructure.persistence.jpa.repository.CurrencyJpaRepository;
import ru.manrovich.cashflow.testing.persistence.AbstractPostgresIntegrationTest;
import ru.manrovich.cashflow.testing.persistence.JpaIntegrationTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@JpaIntegrationTest
@Import({
        CurrencyRepositoryAdapter.class,
        CurrencyQueryPortAdapter.class,
        CurrencyEntityMapper.class
})
class CurrencyPersistenceIntegrationTest extends AbstractPostgresIntegrationTest {

    @Autowired
    private CurrencyRepositoryAdapter currencyRepositoryAdapter;

    @Autowired
    private CurrencyQueryPortAdapter currencyQueryPortAdapter;

    @Autowired
    private CurrencyJpaRepository currencyJpaRepository;

    @Test
    void exists_shouldReturnFalse_whenCurrencyNotSaved() {
        assertFalse(currencyQueryPortAdapter.exists(new CurrencyId("RUB")));
    }

    @Test
    void save_and_findByCode_shouldRoundTripDomainModel() {
        Currency rub = new Currency(new CurrencyId("RUB"), "Russian Ruble", 2, "₽");

        currencyRepositoryAdapter.save(rub);
        currencyJpaRepository.flush();

        assertTrue(currencyQueryPortAdapter.exists(new CurrencyId("RUB")));

        Currency loaded = currencyRepositoryAdapter.findByCode(new CurrencyId("RUB"))
                .orElseThrow(() -> new AssertionError("Currency must be found"));

        assertEquals("RUB", loaded.code().value());
        assertEquals("Russian Ruble", loaded.name());
        assertEquals(2, loaded.scale());
        assertEquals("₽", loaded.symbol());
    }

    @Test
    void saveAll_shouldPersistAll() {
        Currency rub = new Currency(new CurrencyId("RUB"), "Russian Ruble", 2, "₽");
        Currency usd = new Currency(new CurrencyId("USD"), "US Dollar", 2, "$");

        currencyRepositoryAdapter.saveAll(List.of(rub, usd));
        currencyJpaRepository.flush();

        assertTrue(currencyQueryPortAdapter.exists(new CurrencyId("RUB")));
        assertTrue(currencyQueryPortAdapter.exists(new CurrencyId("USD")));
    }

    @Test
    void dbConstraints_shouldRejectTooLongName() {
        String tooLongName = "X".repeat(65);

        Currency bad = new Currency(new CurrencyId("EUR"), tooLongName, 2, "€");

        assertThrows(DataIntegrityViolationException.class, () -> {
            currencyRepositoryAdapter.save(bad);
            currencyJpaRepository.flush();
        });
    }
}
