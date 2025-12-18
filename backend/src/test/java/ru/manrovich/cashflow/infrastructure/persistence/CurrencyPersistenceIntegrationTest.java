package ru.manrovich.cashflow.infrastructure.persistence;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.manrovich.cashflow.application.BudgetApplication;
import ru.manrovich.cashflow.domain.kernel.id.CurrencyId;
import ru.manrovich.cashflow.domain.reference.currency.model.Currency;
import ru.manrovich.cashflow.infrastructure.persistence.jpa.adapter.CurrencyQueryPortAdapter;
import ru.manrovich.cashflow.infrastructure.persistence.jpa.adapter.CurrencyRepositoryAdapter;
import ru.manrovich.cashflow.infrastructure.persistence.jpa.mapper.CurrencyEntityMapper;
import ru.manrovich.cashflow.infrastructure.persistence.jpa.repository.CurrencyJpaRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

// TODO копипаста. Нужно проверить
@Testcontainers
@DataJpaTest
@ContextConfiguration(classes = BudgetApplication.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({
        CurrencyRepositoryAdapter.class,
        CurrencyQueryPortAdapter.class,
        CurrencyEntityMapper.class
})
class CurrencyPersistenceIntegrationTest {

    @Container
    static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("cashflow_test")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);

        // Важно: у тебя Liquibase выключен, значит схему создаёт Hibernate
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
        registry.add("spring.jpa.properties.hibernate.dialect", () -> "org.hibernate.dialect.PostgreSQLDialect");
    }

    @Autowired
    private CurrencyRepositoryAdapter currencyRepositoryAdapter;

    @Autowired
    private CurrencyQueryPortAdapter currencyQueryPortAdapter;

    @Autowired
    private CurrencyJpaRepository currencyJpaRepository; // чтобы flush() делать явно

    @Test
    void exists_shouldReturnFalse_whenCurrencyNotSaved() {
        assertFalse(currencyQueryPortAdapter.exists(new CurrencyId("RUB")));
    }

    @Test
    void save_and_findByCode_shouldRoundTripDomainModel() {
        Currency rub = new Currency(new CurrencyId("RUB"), "Russian Ruble", 2, "₽");

        currencyRepositoryAdapter.save(rub);
        currencyJpaRepository.flush(); // чтобы гарантированно улетело в БД

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
        // В entity у тебя name length = 64, а в домене это не проверяется
        // Проверяем, что БД действительно режет/отбрасывает, а не молча сохраняет
        String tooLongName = "X".repeat(65);

        Currency bad = new Currency(new CurrencyId("EUR"), tooLongName, 2, "€");

        assertThrows(DataIntegrityViolationException.class, () -> {
            currencyRepositoryAdapter.save(bad);
            currencyJpaRepository.flush(); // важно: исключение часто вылетает именно на flush
        });
    }
}
