package ru.manrovich.cashflow.domain.reference.currency.model;

import org.junit.jupiter.api.Test;
import ru.manrovich.cashflow.domain.kernel.exception.ValidationException;
import ru.manrovich.cashflow.domain.kernel.id.CurrencyId;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CurrencyTest {

    @Test
    void shouldCreateCurrency_whenValid() {
        Currency currency = new Currency(new CurrencyId("RUB"), "Russian Ruble", 2, "₽");

        assertEquals("RUB", currency.code().value());
        assertEquals("Russian Ruble", currency.name());
        assertEquals(2, currency.scale());
        assertEquals("₽", currency.symbol());
    }

    @Test
    void shouldAllowNullSymbol() {
        Currency currency = new Currency(new CurrencyId("USD"), "US Dollar", 2, null);
        assertNull(currency.symbol());
    }

    @Test
    void shouldRejectNullCode() {
        assertThrows(ValidationException.class, () ->
                new Currency(null, "US Dollar", 2, "$")
        );
    }

    @Test
    void shouldRejectBlankName() {
        assertThrows(ValidationException.class, () ->
                new Currency(new CurrencyId("USD"), "   ", 2, "$")
        );
    }

    @Test
    void shouldRejectNonIsoCode() {
        assertThrows(ValidationException.class, () ->
                new Currency(new CurrencyId("USDT"), "Tether", 6, null)
        );
    }

    @Test
    void shouldRejectBlankSymbol_whenProvided() {
        assertThrows(ValidationException.class, () ->
                new Currency(new CurrencyId("EUR"), "Euro", 2, "   ")
        );
    }

    @Test
    void shouldRejectInvalidScale() {
        assertThrows(ValidationException.class, () ->
                new Currency(new CurrencyId("USD"), "US Dollar", -1, "$")
        );
        assertThrows(ValidationException.class, () ->
                new Currency(new CurrencyId("USD"), "US Dollar", 19, "$")
        );
    }

}
