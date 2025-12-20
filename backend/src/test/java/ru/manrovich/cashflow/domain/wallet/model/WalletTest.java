package ru.manrovich.cashflow.domain.wallet.model;

import org.junit.jupiter.api.Test;
import ru.manrovich.cashflow.domain.kernel.exception.ValidationException;
import ru.manrovich.cashflow.domain.kernel.id.CurrencyId;
import ru.manrovich.cashflow.domain.kernel.id.UserId;
import ru.manrovich.cashflow.domain.kernel.id.WalletId;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class WalletTest {

    @Test
    void shouldCreateWallet() {
        Wallet wallet = new Wallet(
                new WalletId(UUID.randomUUID()),
                new UserId(UUID.randomUUID()),
                new WalletName("Main"),
                new CurrencyId("RUB")
        );

        assertEquals("Main", wallet.name().value());
        assertEquals("RUB", wallet.currencyId().value());
    }

    @Test
    void shouldRenameWallet() {
        Wallet wallet = new Wallet(
                new WalletId(UUID.randomUUID()),
                new UserId(UUID.randomUUID()),
                new WalletName("Old"),
                new CurrencyId("RUB")
        );

        wallet.rename(new WalletName("New"));
        assertEquals("New", wallet.name().value());
    }

    @Test
    void shouldFailWhenNulls() {
        assertThrows(ValidationException.class, () -> new Wallet(
                null,
                new UserId(UUID.randomUUID()),
                new WalletName("Main"),
                new CurrencyId("RUB")
        ));
    }
}
