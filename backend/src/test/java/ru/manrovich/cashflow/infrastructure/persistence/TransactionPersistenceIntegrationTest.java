package ru.manrovich.cashflow.infrastructure.persistence;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import ru.manrovich.cashflow.domain.kernel.id.CurrencyId;
import ru.manrovich.cashflow.domain.kernel.id.TransactionId;
import ru.manrovich.cashflow.domain.kernel.id.WalletId;
import ru.manrovich.cashflow.domain.kernel.money.Money;
import ru.manrovich.cashflow.domain.transaction.model.Transaction;
import ru.manrovich.cashflow.domain.transaction.model.TransactionType;
import ru.manrovich.cashflow.infrastructure.persistence.jpa.adapter.TransactionQueryPortAdapter;
import ru.manrovich.cashflow.infrastructure.persistence.jpa.adapter.TransactionRepositoryAdapter;
import ru.manrovich.cashflow.infrastructure.persistence.jpa.entity.TransactionEntity;
import ru.manrovich.cashflow.infrastructure.persistence.jpa.mapper.TransactionEntityMapper;
import ru.manrovich.cashflow.infrastructure.persistence.jpa.repository.TransactionJpaRepository;
import ru.manrovich.cashflow.testing.persistence.AbstractPostgresIntegrationTest;
import ru.manrovich.cashflow.testing.persistence.JpaIntegrationTest;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static ru.manrovich.cashflow.testing.data.TestUsers.USER_1;
import static ru.manrovich.cashflow.testing.data.TestUsers.USER_2;

@JpaIntegrationTest
@Import({
        TransactionRepositoryAdapter.class,
        TransactionQueryPortAdapter.class,
        TransactionEntityMapper.class
})
class TransactionPersistenceIntegrationTest extends AbstractPostgresIntegrationTest {

    @Autowired
    private TransactionRepositoryAdapter transactionRepositoryAdapter;

    @Autowired
    private TransactionQueryPortAdapter transactionQueryPortAdapter;

    @Autowired
    private TransactionJpaRepository transactionJpaRepository;

    @Test
    void exists_shouldReturnFalse_whenNotSaved() {
        TransactionId id = new TransactionId(UUID.randomUUID());
        assertFalse(transactionQueryPortAdapter.exists(USER_1, id));
    }

    @Test
    void save_and_findById_shouldRoundTripDomainModel() {
        TransactionId id = new TransactionId(UUID.randomUUID());

        Transaction tx = new Transaction(
                id,
                USER_1,
                new WalletId(UUID.randomUUID()),
                null,
                TransactionType.INCOME,
                new Money(new BigDecimal("10.00"), new CurrencyId("RUB")),
                Instant.parse("2025-01-01T10:00:00Z")
        );

        transactionRepositoryAdapter.save(tx);
        transactionJpaRepository.flush();

        assertTrue(transactionQueryPortAdapter.exists(USER_1, id));

        Transaction loaded = transactionRepositoryAdapter.findById(USER_1, id)
                .orElseThrow(() -> new AssertionError("Transaction must be found"));

        assertEquals(id.value(), loaded.id().value());
        assertEquals(USER_1.value(), loaded.ownerId().value());
        assertEquals(tx.walletId().value(), loaded.walletId().value());
        assertEquals(TransactionType.INCOME, loaded.type());
        assertEquals("RUB", loaded.money().currencyId().value());
        assertEquals(0, loaded.money().amount().compareTo(new BigDecimal("10.00")));
        assertEquals("2025-01-01T10:00:00Z", loaded.occurredAt().toString());
    }

    @Test
    void existsByWalletId_shouldBeTrue_afterSave() {
        WalletId walletId = new WalletId(UUID.randomUUID());

        Transaction tx = new Transaction(
                new TransactionId(UUID.randomUUID()),
                USER_1,
                walletId,
                null,
                TransactionType.INCOME,
                new Money(new BigDecimal("1"), new CurrencyId("RUB")),
                Instant.parse("2025-01-01T10:00:00Z")
        );

        transactionRepositoryAdapter.save(tx);
        transactionJpaRepository.flush();

        assertTrue(transactionQueryPortAdapter.existsByWalletId(USER_1, walletId));
        assertFalse(transactionQueryPortAdapter.existsByWalletId(USER_2, walletId));
    }

    @Test
    void sumAmountsByWalletId_shouldReturnSignedSum_basedOnType() {
        WalletId walletId = new WalletId(UUID.randomUUID());

        transactionRepositoryAdapter.save(new Transaction(
                new TransactionId(UUID.randomUUID()),
                USER_1,
                walletId,
                null,
                TransactionType.INCOME,
                new Money(new BigDecimal("100.00"), new CurrencyId("RUB")),
                Instant.parse("2025-01-01T10:00:00Z")
        ));

        transactionRepositoryAdapter.save(new Transaction(
                new TransactionId(UUID.randomUUID()),
                USER_1,
                walletId,
                null,
                TransactionType.EXPENSE,
                new Money(new BigDecimal("30.00"), new CurrencyId("RUB")),
                Instant.parse("2025-01-02T10:00:00Z")
        ));

        transactionJpaRepository.flush();

        BigDecimal sum = transactionQueryPortAdapter.sumAmountsByWalletId(USER_1, walletId);
        assertEquals(0, sum.compareTo(new BigDecimal("70.00")));

        // другой пользователь не должен видеть чужие суммы
        BigDecimal sum2 = transactionQueryPortAdapter.sumAmountsByWalletId(USER_2, walletId);
        assertEquals(0, sum2.compareTo(BigDecimal.ZERO));
    }

    @Test
    void deleteById_shouldDeleteOnlyForOwner() {
        TransactionId id = new TransactionId(UUID.randomUUID());

        transactionRepositoryAdapter.save(new Transaction(
                id,
                USER_1,
                new WalletId(UUID.randomUUID()),
                null,
                TransactionType.INCOME,
                new Money(new BigDecimal("5"), new CurrencyId("RUB")),
                Instant.parse("2025-01-01T10:00:00Z")
        ));
        transactionJpaRepository.flush();

        transactionRepositoryAdapter.deleteById(USER_2, id);
        transactionJpaRepository.flush();

        assertTrue(transactionQueryPortAdapter.exists(USER_1, id));

        transactionRepositoryAdapter.deleteById(USER_1, id);
        transactionJpaRepository.flush();

        assertFalse(transactionQueryPortAdapter.exists(USER_1, id));
    }

    @Test
    void dbConstraints_shouldRejectNullWalletId() {
        TransactionEntity bad = new TransactionEntity(
                UUID.randomUUID(),
                USER_1.value(),
                null,
                null,
                TransactionType.INCOME.name(),
                new BigDecimal("10"),
                "RUB",
                Instant.parse("2025-01-01T10:00:00Z")
        );

        assertThrows(DataIntegrityViolationException.class, () -> {
            transactionJpaRepository.save(bad);
            transactionJpaRepository.flush();
        });
    }

    @Test
    void dbConstraints_shouldRejectNullCurrencyCode() {
        TransactionEntity bad = new TransactionEntity(
                UUID.randomUUID(),
                USER_1.value(),
                UUID.randomUUID(),
                null,
                TransactionType.INCOME.name(),
                new BigDecimal("10"),
                null,
                Instant.parse("2025-01-01T10:00:00Z")
        );

        assertThrows(DataIntegrityViolationException.class, () -> {
            transactionJpaRepository.save(bad);
            transactionJpaRepository.flush();
        });
    }

    @Test
    void dbConstraints_shouldRejectTooLongCurrencyCode() {
        String tooLong = "X".repeat(33);

        TransactionEntity bad = new TransactionEntity(
                UUID.randomUUID(),
                USER_1.value(),
                UUID.randomUUID(),
                null,
                TransactionType.INCOME.name(),
                new BigDecimal("10"),
                tooLong,
                Instant.parse("2025-01-01T10:00:00Z")
        );

        assertThrows(DataIntegrityViolationException.class, () -> {
            transactionJpaRepository.save(bad);
            transactionJpaRepository.flush();
        });
    }
}
