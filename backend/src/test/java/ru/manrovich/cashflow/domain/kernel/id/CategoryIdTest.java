package ru.manrovich.cashflow.domain.kernel.id;

import org.junit.jupiter.api.Test;
import ru.manrovich.cashflow.domain.kernel.exception.ValidationException;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CategoryIdTest {

    @Test
    void shouldCreate_whenUuidNotNull() {
        UUID uuid = UUID.randomUUID();

        CategoryId id = new CategoryId(uuid);

        assertEquals(uuid, id.value());
    }

    @Test
    void shouldThrow_whenUuidIsNull() {
        ValidationException ex = assertThrows(ValidationException.class, () -> new CategoryId(null));
        assertTrue(ex.getMessage().toLowerCase().contains("categoryid"));
    }

    @Test
    void equalsAndHashCode_shouldWork_forSameUuid() {
        UUID uuid = UUID.randomUUID();

        CategoryId a = new CategoryId(uuid);
        CategoryId b = new CategoryId(uuid);

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }
}
