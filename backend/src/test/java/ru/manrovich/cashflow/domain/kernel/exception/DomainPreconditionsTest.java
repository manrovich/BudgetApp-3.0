package ru.manrovich.cashflow.domain.kernel.exception;

import org.junit.jupiter.api.Test;
import ru.manrovich.cashflow.domain.kernel.validation.DomainPreconditions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DomainPreconditionsTest {

    @Test
    void notNullShouldReturnValue() {
        String value = DomainPreconditions.notNull("x", "must not be null");
        assertEquals("x", value);
    }

    @Test
    void notNullShouldThrowWhenNull() {
        assertThrows(ValidationException.class, () ->
                DomainPreconditions.notNull(null, "must not be null")
        );
    }

    @Test
    void notBlankShouldThrowWhenBlank() {
        assertThrows(ValidationException.class, () ->
                DomainPreconditions.notBlank("   ", "must not be blank")
        );
    }

    @Test
    void checkShouldThrowWhenFalse() {
        assertThrows(ValidationException.class, () ->
                DomainPreconditions.check(false, "condition failed")
        );
    }
}