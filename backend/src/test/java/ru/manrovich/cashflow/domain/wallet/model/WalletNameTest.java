package ru.manrovich.cashflow.domain.wallet.model;

import org.junit.jupiter.api.Test;
import ru.manrovich.cashflow.domain.kernel.exception.ValidationException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class WalletNameTest {

    @Test
    void shouldCreateWhenValid() {
        WalletName name = new WalletName("Main");
        assertEquals("Main", name.value());
    }

    @Test
    void shouldFailWhenBlank() {
        assertThrows(ValidationException.class, () -> new WalletName("   "));
    }

    @Test
    void shouldFailWhenNull() {
        assertThrows(ValidationException.class, () -> new WalletName(null));
    }

    @Test
    void shouldFailWhenTooLong() {
        String tooLongName = "a".repeat(65);
        assertThrows(ValidationException.class, () -> new WalletName(tooLongName));
    }

    @Test
    void shouldAllowExactMaxLength() {
        String name = "a".repeat(64);
        assertDoesNotThrow(() -> new WalletName(name));
    }
}
