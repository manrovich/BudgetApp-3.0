package ru.manrovich.cashflow.domain.reference.category.model;

import org.junit.jupiter.api.Test;
import ru.manrovich.cashflow.domain.kernel.exception.ValidationException;
import ru.manrovich.cashflow.domain.kernel.id.CategoryId;
import ru.manrovich.cashflow.domain.kernel.id.UserId;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CategoryTest {

    private static final UserId OWNER = new UserId(UUID.fromString("00000000-0000-0000-0000-000000000001"));

    @Test
    void shouldCreateCategory_whenAllFieldsValid() {
        CategoryId id = new CategoryId(UUID.randomUUID());

        Category category = new Category(id, OWNER, new CategoryName("Food"));

        assertEquals(id, category.id());
        assertEquals(OWNER, category.ownerId());
        assertEquals("Food", category.name().value());
    }

    @Test
    void shouldThrow_whenIdIsNull() {
        ValidationException ex = assertThrows(ValidationException.class, () ->
                new Category(null, OWNER, new CategoryName("Food"))
        );
        assertTrue(ex.getMessage().toLowerCase().contains("categoryid"));
    }

    @Test
    void shouldThrow_whenOwnerIdIsNull() {
        CategoryId id = new CategoryId(UUID.randomUUID());

        ValidationException ex = assertThrows(ValidationException.class, () ->
                new Category(id, null, new CategoryName("Food"))
        );
        assertTrue(ex.getMessage().toLowerCase().contains("owner"));
    }

    @Test
    void shouldThrow_whenNameIsNull() {
        CategoryId id = new CategoryId(UUID.randomUUID());

        ValidationException ex = assertThrows(ValidationException.class, () ->
                new Category(id, OWNER, null)
        );
        assertTrue(ex.getMessage().toLowerCase().contains("name"));
    }

    @Test
    void rename_shouldChangeName_whenNewNameValid() {
        CategoryId id = new CategoryId(UUID.randomUUID());
        Category category = new Category(id, OWNER, new CategoryName("Food"));

        category.rename(new CategoryName("Groceries"));

        assertEquals("Groceries", category.name().value());
    }

    @Test
    void rename_shouldThrow_whenNewNameIsNull() {
        CategoryId id = new CategoryId(UUID.randomUUID());
        Category category = new Category(id, OWNER, new CategoryName("Food"));

        ValidationException ex = assertThrows(ValidationException.class, () -> category.rename(null));
        assertTrue(ex.getMessage().toLowerCase().contains("name"));
    }

    @Test
    void shouldValidateCategoryName_blankIsRejected() {
        assertThrows(ValidationException.class, () -> new CategoryName("   "));
    }

    @Test
    void shouldValidateCategoryName_tooLongIsRejected() {
        String tooLong = "X".repeat(129);
        assertThrows(ValidationException.class, () -> new CategoryName(tooLong));
    }
}
