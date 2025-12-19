package ru.manrovich.cashflow.domain.kernel.id;

import org.junit.jupiter.api.Test;
import ru.manrovich.cashflow.domain.kernel.exception.ValidationException;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TransactionIdTest {

    @Test
    void constructor_shouldThrow_whenNull() {
        assertThrows(ValidationException.class, () -> new TransactionId(null));
    }

    @Test
    void equals_shouldBeTrue_forSameUuid() {
        UUID uuid = UUID.randomUUID();
        assertEquals(new TransactionId(uuid), new TransactionId(uuid));
    }
}
