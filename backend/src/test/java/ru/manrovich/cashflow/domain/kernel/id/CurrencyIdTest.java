package ru.manrovich.cashflow.domain.kernel.id;

import org.junit.jupiter.api.Test;
import ru.manrovich.cashflow.domain.kernel.exception.ValidationException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CurrencyIdTest {

    @Test
    void shouldBeEqualIgnoringCase() {
        CurrencyId a = new CurrencyId("rub");
        CurrencyId b = new CurrencyId("RUB");

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertEquals("RUB", a.value());
        assertEquals("RUB", b.value());
    }

    @Test
    void shouldTrimSpaces() {
        CurrencyId id = new CurrencyId("  rub  ");
        assertEquals("RUB", id.value());
    }

    @Test
    void shouldNormalizeCustomCodesToo() {
        CurrencyId id = new CurrencyId("usr:mycoin");
        assertEquals("USR:MYCOIN", id.value());
    }

    @Test
    void shouldThrowWhenNull() {
        assertThrows(ValidationException.class, () -> new CurrencyId(null));
    }

    @Test
    void shouldThrowWhenBlank() {
        assertThrows(ValidationException.class, () -> new CurrencyId("   "));
    }
}