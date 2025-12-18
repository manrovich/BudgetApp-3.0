package ru.manrovich.cashflow.domain.kernel.money;

import org.junit.jupiter.api.Test;
import ru.manrovich.cashflow.domain.kernel.id.CurrencyId;
import ru.manrovich.cashflow.domain.kernel.exception.ValidationException;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class MoneyTest {

    @Test
    void shouldThrowWhenAmountIsNull() {
        assertThrows(ValidationException.class, () ->
                new Money(null, new CurrencyId("RUB")));
    }

    @Test
    void shouldThrowWhenAmountIsZero() {
        assertThrows(ValidationException.class, () ->
                new Money(BigDecimal.ZERO, new CurrencyId("RUB")));
    }

    @Test
    void shouldThrowWhenAmountIsZeroWithScale() {
        assertThrows(ValidationException.class, () ->
                new Money(new BigDecimal("0.00"), new CurrencyId("RUB"))
        );
    }

    @Test
    void shouldThrowWhenCurrencyIdIsNull() {
        assertThrows(ValidationException.class, () ->
                new Money(new BigDecimal("10"), null)
        );
    }

    @Test
    void shouldCreateMoneyWhenValid() {
        assertDoesNotThrow(() ->
                new Money(new BigDecimal("10.50"), new CurrencyId("rub"))
        );
    }
}
