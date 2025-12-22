package ru.manrovich.cashflow.domain.transaction.port.filter;

import org.junit.jupiter.api.Test;
import ru.manrovich.cashflow.domain.kernel.exception.ValidationException;
import ru.manrovich.cashflow.domain.kernel.id.UserId;
import ru.manrovich.cashflow.domain.kernel.id.WalletId;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TransactionFilterTest {

    @Test
    void constructor_shouldThrowValidation_whenOwnerIdNull() {
        assertThrows(ValidationException.class, () -> new TransactionFilter(
                null,
                null,
                null,
                null
        ));
    }

    @Test
    void constructor_shouldThrowValidation_whenFromAfterTo() {
        UserId ownerId = new UserId(UUID.randomUUID());

        Instant from = Instant.parse("2025-12-10T00:00:00Z");
        Instant to = Instant.parse("2025-12-01T00:00:00Z");

        assertThrows(ValidationException.class, () -> new TransactionFilter(
                ownerId,
                null,
                from,
                to
        ));
    }

    @Test
    void constructor_shouldAllowNullWalletFromTo() {
        UserId ownerId = new UserId(UUID.randomUUID());

        TransactionFilter filter = assertDoesNotThrow(() -> new TransactionFilter(
                ownerId,
                null,
                null,
                null
        ));

        assertEquals(ownerId, filter.ownerId());
        assertNull(filter.walletId());
        assertNull(filter.from());
        assertNull(filter.to());
    }

    @Test
    void constructor_shouldAllowOnlyFromProvided() {
        UserId ownerId = new UserId(UUID.randomUUID());
        Instant from = Instant.parse("2025-12-01T00:00:00Z");

        TransactionFilter filter = assertDoesNotThrow(() -> new TransactionFilter(
                ownerId,
                null,
                from,
                null
        ));

        assertEquals(ownerId, filter.ownerId());
        assertEquals(from, filter.from());
        assertNull(filter.to());
    }

    @Test
    void constructor_shouldAllowOnlyToProvided() {
        UserId ownerId = new UserId(UUID.randomUUID());
        Instant to = Instant.parse("2025-12-31T23:59:59Z");

        TransactionFilter filter = assertDoesNotThrow(() -> new TransactionFilter(
                ownerId,
                null,
                null,
                to
        ));

        assertEquals(ownerId, filter.ownerId());
        assertNull(filter.from());
        assertEquals(to, filter.to());
    }

    @Test
    void constructor_shouldAllowFromEqualsTo() {
        UserId ownerId = new UserId(UUID.randomUUID());
        Instant instant = Instant.parse("2025-12-01T00:00:00Z");

        TransactionFilter filter = assertDoesNotThrow(() -> new TransactionFilter(
                ownerId,
                null,
                instant,
                instant
        ));

        assertEquals(ownerId, filter.ownerId());
        assertEquals(instant, filter.from());
        assertEquals(instant, filter.to());
    }

    @Test
    void constructor_shouldAllowWalletIdProvided() {
        UserId ownerId = new UserId(UUID.randomUUID());
        WalletId walletId = new WalletId(UUID.randomUUID());

        TransactionFilter filter = assertDoesNotThrow(() -> new TransactionFilter(
                ownerId,
                walletId,
                null,
                null
        ));

        assertEquals(ownerId, filter.ownerId());
        assertEquals(walletId, filter.walletId());
    }
}
