package ru.manrovich.cashflow.domain.kernel.money;

import org.junit.jupiter.api.Test;
import ru.manrovich.cashflow.domain.kernel.exception.ValidationException;
import ru.manrovich.cashflow.domain.kernel.id.CurrencyId;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MoneyTest {

    private static final CurrencyId RUB = new CurrencyId("RUB");
    private static final CurrencyId USD = new CurrencyId("USD");

    @Test
    void constructor_shouldThrow_whenAmountIsNull() {
        assertThrows(ValidationException.class, () -> new Money(null, RUB));
    }

    @Test
    void constructor_shouldThrow_whenCurrencyIdIsNull() {
        assertThrows(ValidationException.class, () -> new Money(new BigDecimal("10"), null));
    }

    @Test
    void constructor_shouldAllowZeroAmount() {
        assertDoesNotThrow(() -> new Money(BigDecimal.ZERO, RUB));
        assertDoesNotThrow(() -> new Money(new BigDecimal("0.00"), RUB));
    }

    @Test
    void constructor_shouldAllowNegativeAmount() {
        assertDoesNotThrow(() -> new Money(new BigDecimal("-10.50"), RUB));
    }

    @Test
    void isPositive_isNegative_shouldWork() {
        assertTrue(new Money(new BigDecimal("1"), RUB).isPositive());
        assertFalse(new Money(new BigDecimal("1"), RUB).isNegative());

        assertTrue(new Money(new BigDecimal("-1"), RUB).isNegative());
        assertFalse(new Money(new BigDecimal("-1"), RUB).isPositive());

        assertFalse(new Money(BigDecimal.ZERO, RUB).isPositive());
        assertFalse(new Money(BigDecimal.ZERO, RUB).isNegative());
    }

    @Test
    void negate_shouldFlipSign() {
        Money m = new Money(new BigDecimal("10.50"), RUB);

        Money negated = m.negate();

        assertBigDecimalEquals("-10.50", negated.amount());
        assertEquals(RUB, negated.currencyId());
    }

    @Test
    void abs_shouldReturnPositiveValue_whenNegative() {
        Money m = new Money(new BigDecimal("-10.50"), RUB);

        Money abs = m.abs();

        assertBigDecimalEquals("10.50", abs.amount());
        assertEquals(RUB, abs.currencyId());
    }

    @Test
    void abs_shouldKeepValue_whenPositiveOrZero() {
        Money positive = new Money(new BigDecimal("10.50"), RUB);
        Money zero = new Money(BigDecimal.ZERO, RUB);

        assertBigDecimalEquals("10.50", positive.abs().amount());
        assertBigDecimalEquals("0", zero.abs().amount());
    }

    @Test
    void add_shouldSumAmounts_whenSameCurrency() {
        Money a = new Money(new BigDecimal("10.25"), RUB);
        Money b = new Money(new BigDecimal("2.75"), RUB);

        Money sum = a.add(b);

        assertBigDecimalEquals("13.00", sum.amount());
        assertEquals(RUB, sum.currencyId());
    }

    @Test
    void add_shouldAllowSumToBeZero() {
        Money a = new Money(new BigDecimal("10"), RUB);
        Money b = new Money(new BigDecimal("-10"), RUB);

        Money sum = a.add(b);

        assertBigDecimalEquals("0", sum.amount());
        assertEquals(RUB, sum.currencyId());
    }

    @Test
    void add_shouldThrow_whenOtherIsNull() {
        Money a = new Money(new BigDecimal("10"), RUB);

        assertThrows(ValidationException.class, () -> a.add(null));
    }

    @Test
    void add_shouldThrow_whenCurrencyMismatch() {
        Money a = new Money(new BigDecimal("10"), RUB);
        Money b = new Money(new BigDecimal("1"), USD);

        assertThrows(ValidationException.class, () -> a.add(b));
    }

    @Test
    void subtract_shouldSubtractAmounts_whenSameCurrency() {
        Money a = new Money(new BigDecimal("10.25"), RUB);
        Money b = new Money(new BigDecimal("2.75"), RUB);

        Money result = a.subtract(b);

        assertBigDecimalEquals("7.50", result.amount());
        assertEquals(RUB, result.currencyId());
    }

    @Test
    void subtract_shouldAllowResultToBeZero() {
        Money a = new Money(new BigDecimal("10"), RUB);
        Money b = new Money(new BigDecimal("10"), RUB);

        Money result = a.subtract(b);

        assertBigDecimalEquals("0", result.amount());
        assertEquals(RUB, result.currencyId());
    }

    @Test
    void subtract_shouldThrow_whenOtherIsNull() {
        Money a = new Money(new BigDecimal("10"), RUB);

        assertThrows(ValidationException.class, () -> a.subtract(null));
    }

    @Test
    void subtract_shouldThrow_whenCurrencyMismatch() {
        Money a = new Money(new BigDecimal("10"), RUB);
        Money b = new Money(new BigDecimal("1"), USD);

        assertThrows(ValidationException.class, () -> a.subtract(b));
    }

    private static void assertBigDecimalEquals(String expected, BigDecimal actual) {
        assertEquals(0, actual.compareTo(new BigDecimal(expected)),
                "Expected " + expected + " but was " + actual);
    }
}
