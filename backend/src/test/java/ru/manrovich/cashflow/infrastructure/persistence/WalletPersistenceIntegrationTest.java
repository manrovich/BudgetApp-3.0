package ru.manrovich.cashflow.infrastructure.persistence;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import ru.manrovich.cashflow.domain.kernel.id.CurrencyId;
import ru.manrovich.cashflow.domain.kernel.id.WalletId;
import ru.manrovich.cashflow.domain.wallet.model.Wallet;
import ru.manrovich.cashflow.domain.wallet.model.WalletName;
import ru.manrovich.cashflow.infrastructure.persistence.jpa.adapter.WalletQueryPortAdapter;
import ru.manrovich.cashflow.infrastructure.persistence.jpa.adapter.WalletRepositoryAdapter;
import ru.manrovich.cashflow.infrastructure.persistence.jpa.entity.WalletEntity;
import ru.manrovich.cashflow.infrastructure.persistence.jpa.mapper.WalletEntityMapper;
import ru.manrovich.cashflow.infrastructure.persistence.jpa.repository.WalletJpaRepository;
import ru.manrovich.cashflow.testing.persistence.AbstractPostgresIntegrationTest;
import ru.manrovich.cashflow.testing.persistence.JpaIntegrationTest;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static ru.manrovich.cashflow.testing.data.TestUsers.USER_1;
import static ru.manrovich.cashflow.testing.data.TestUsers.USER_2;

@JpaIntegrationTest
@Import({
        WalletRepositoryAdapter.class,
        WalletQueryPortAdapter.class,
        WalletEntityMapper.class
})
class WalletPersistenceIntegrationTest extends AbstractPostgresIntegrationTest {

    @Autowired
    private WalletRepositoryAdapter walletRepositoryAdapter;

    @Autowired
    private WalletQueryPortAdapter walletQueryPortAdapter;

    @Autowired
    private WalletJpaRepository walletJpaRepository;

    @Test
    void exists_shouldReturnFalse_whenWalletNotSaved() {
        WalletId id = new WalletId(UUID.randomUUID());
        assertFalse(walletQueryPortAdapter.exists(USER_1, id));
    }

    @Test
    void save_and_findById_shouldRoundTripDomainModel() {
        WalletId id = new WalletId(UUID.randomUUID());
        Wallet wallet = new Wallet(
                id,
                USER_1,
                new WalletName("Main"),
                new CurrencyId("RUB")
        );

        walletRepositoryAdapter.save(wallet);
        walletJpaRepository.flush();

        assertTrue(walletQueryPortAdapter.exists(USER_1, id));
        assertEquals("RUB", walletQueryPortAdapter.getCurrencyId(USER_1, id).value());

        Wallet loaded = walletRepositoryAdapter.findById(USER_1, id)
                .orElseThrow(() -> new AssertionError("Wallet must be found"));

        assertEquals(id.value(), loaded.id().value());
        assertEquals(USER_1.value(), loaded.ownerId().value());
        assertEquals("Main", loaded.name().value());
        assertEquals("RUB", loaded.currencyId().value());
    }

    @Test
    void findById_shouldNotReturnWallet_forAnotherUser() {
        WalletId id = new WalletId(UUID.randomUUID());
        walletRepositoryAdapter.save(new Wallet(id, USER_1, new WalletName("Main"), new CurrencyId("RUB")));
        walletJpaRepository.flush();

        assertTrue(walletRepositoryAdapter.findById(USER_1, id).isPresent());
        assertTrue(walletRepositoryAdapter.findById(USER_2, id).isEmpty());
    }

    @Test
    void deleteById_shouldDeleteOnlyForOwner() {
        WalletId id = new WalletId(UUID.randomUUID());

        walletRepositoryAdapter.save(new Wallet(id, USER_1, new WalletName("Main"), new CurrencyId("RUB")));
        walletJpaRepository.flush();

        walletRepositoryAdapter.deleteById(USER_2, id);
        walletJpaRepository.flush();

        assertTrue(walletQueryPortAdapter.exists(USER_1, id));

        walletRepositoryAdapter.deleteById(USER_1, id);
        walletJpaRepository.flush();

        assertFalse(walletQueryPortAdapter.exists(USER_1, id));
    }

    @Test
    void dbConstraints_shouldRejectNullOwnerId() {
        WalletEntity bad = new WalletEntity(
                UUID.randomUUID(),
                null,
                "Main",
                "RUB"
        );

        assertThrows(DataIntegrityViolationException.class, () -> {
            walletJpaRepository.save(bad);
            walletJpaRepository.flush();
        });
    }

    @Test
    void dbConstraints_shouldRejectNullName() {
        WalletEntity bad = new WalletEntity(
                UUID.randomUUID(),
                USER_1.value(),
                null,
                "RUB"
        );

        assertThrows(DataIntegrityViolationException.class, () -> {
            walletJpaRepository.save(bad);
            walletJpaRepository.flush();
        });
    }

    @Test
    void dbConstraints_shouldRejectTooLongName() {
        WalletEntity bad = new WalletEntity(
                UUID.randomUUID(),
                USER_1.value(),
                "X".repeat(65),
                "RUB"
        );

        assertThrows(DataIntegrityViolationException.class, () -> {
            walletJpaRepository.save(bad);
            walletJpaRepository.flush();
        });
    }

    @Test
    void dbConstraints_shouldRejectNullCurrencyCode() {
        WalletEntity bad = new WalletEntity(
                UUID.randomUUID(),
                USER_1.value(),
                "Main",
                null
        );

        assertThrows(DataIntegrityViolationException.class, () -> {
            walletJpaRepository.save(bad);
            walletJpaRepository.flush();
        });
    }

    @Test
    void dbConstraints_shouldRejectTooLongCurrencyCode() {
        WalletEntity bad = new WalletEntity(
                UUID.randomUUID(),
                USER_1.value(),
                "Main",
                "RUBX"
        );

        assertThrows(DataIntegrityViolationException.class, () -> {
            walletJpaRepository.save(bad);
            walletJpaRepository.flush();
        });
    }
}
