package ru.manrovich.cashflow.domain.transaction.model;

import org.junit.jupiter.api.Test;
import ru.manrovich.cashflow.domain.kernel.exception.ValidationException;
import ru.manrovich.cashflow.domain.kernel.id.CurrencyId;
import ru.manrovich.cashflow.domain.kernel.id.TransactionId;
import ru.manrovich.cashflow.domain.kernel.id.WalletId;
import ru.manrovich.cashflow.domain.kernel.money.Money;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static ru.manrovich.cashflow.testing.data.TestUsers.USER_1;

class TransactionTest {

    @Test
    void shouldCreate_whenValid() {
        Transaction tx = new Transaction(
                new TransactionId(UUID.randomUUID()),
                USER_1,
                new WalletId(UUID.randomUUID()),
                null,
                TransactionType.INCOME,
                new Money(new BigDecimal("10.50"), new CurrencyId("RUB")),
                Instant.parse("2025-01-01T10:00:00Z")
        );

        assertNotNull(tx.id());
        assertEquals(USER_1.value(), tx.ownerId().value());
        assertNotNull(tx.walletId());
        assertNull(tx.categoryId());
        assertEquals(TransactionType.INCOME, tx.type());
    }

    @Test
    void shouldThrow_whenOccurredAtNull() {
        assertThrows(ValidationException.class, () -> new Transaction(
                new TransactionId(UUID.randomUUID()),
                USER_1,
                new WalletId(UUID.randomUUID()),
                null,
                TransactionType.INCOME,
                new Money(new BigDecimal("10"), new CurrencyId("RUB")),
                null
        ));
    }

    @Test
    void shouldThrow_whenAmountIsZero() {
        assertThrows(ValidationException.class, () -> new Transaction(
                new TransactionId(UUID.randomUUID()),
                USER_1,
                new WalletId(UUID.randomUUID()),
                null,
                TransactionType.INCOME,
                new Money(BigDecimal.ZERO, new CurrencyId("RUB")),
                Instant.parse("2025-01-01T10:00:00Z")
        ));
    }

    @Test
    void shouldThrow_whenAmountIsNegative() {
        assertThrows(ValidationException.class, () -> new Transaction(
                new TransactionId(UUID.randomUUID()),
                USER_1,
                new WalletId(UUID.randomUUID()),
                null,
                TransactionType.EXPENSE,
                new Money(new BigDecimal("-1"), new CurrencyId("RUB")),
                Instant.parse("2025-01-01T10:00:00Z")
        ));
    }

    @Test
    void effect_shouldReturnSignedMoney_basedOnType() {
        Money money = new Money(new BigDecimal("10.50"), new CurrencyId("RUB"));

        Transaction income = new Transaction(
                new TransactionId(UUID.randomUUID()),
                USER_1,
                new WalletId(UUID.randomUUID()),
                null,
                TransactionType.INCOME,
                money,
                Instant.parse("2025-01-01T10:00:00Z")
        );

        Transaction expense = new Transaction(
                new TransactionId(UUID.randomUUID()),
                USER_1,
                new WalletId(UUID.randomUUID()),
                null,
                TransactionType.EXPENSE,
                money,
                Instant.parse("2025-01-01T10:00:00Z")
        );

        assertEquals(0, income.effect().amount().compareTo(new BigDecimal("10.50")));
        assertEquals(0, expense.effect().amount().compareTo(new BigDecimal("-10.50")));
        assertEquals(income.money().currencyId(), expense.effect().currencyId());
    }
}
