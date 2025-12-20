package ru.manrovich.cashflow.domain.reference.category.model;

import org.junit.jupiter.api.Test;
import ru.manrovich.cashflow.domain.kernel.exception.ValidationException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CategoryNameTest {

    @Test
    void shouldCreate_whenValidName() {
        CategoryName name = new CategoryName("Food");

        assertEquals("Food", name.value());
    }

    @Test
    void shouldNormalize_whenHasLeadingOrTrailingSpaces() {
        CategoryName name = new CategoryName("  Food  ");

        assertEquals("Food", name.value());
    }

    @Test
    void shouldThrow_whenNull() {
        ValidationException ex = assertThrows(ValidationException.class, () -> new CategoryName(null));
        assertTrue(ex.getMessage().toLowerCase().contains("must not be blank"));
    }

    @Test
    void shouldThrow_whenBlank() {
        assertThrows(ValidationException.class, () -> new CategoryName("   "));
    }

    @Test
    void shouldAllow_whenLengthIsExactlyMax_afterTrim() {
        String value = "X".repeat(128);

        CategoryName name = new CategoryName(value);

        assertEquals(value, name.value());
        assertEquals(128, name.value().length());
    }

    @Test
    void shouldThrow_whenTooLong_afterTrim() {
        String tooLong = "X".repeat(129);
        assertThrows(ValidationException.class, () -> new CategoryName(tooLong));
    }

    @Test
    void shouldThrow_whenTooLong_evenIfSpacesWouldBeTrimmed() {
        String tooLongAfterTrim = " " + "X".repeat(129) + " ";
        assertThrows(ValidationException.class, () -> new CategoryName(tooLongAfterTrim));
    }

    @Test
    void equalsAndHashCode_shouldWork_forSameNormalizedValue() {
        CategoryName a = new CategoryName("  Food ");
        CategoryName b = new CategoryName("Food");

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertEquals("Food", a.value());
    }
}
