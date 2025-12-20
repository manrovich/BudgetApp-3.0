package ru.manrovich.cashflow.domain.kernel.validation;

import ru.manrovich.cashflow.domain.kernel.exception.ValidationException;

public final class DomainPreconditions {

    private DomainPreconditions() {
    }

    public static <T> T notNull(T value, String message) {
        if (value == null) {
            throw new ValidationException(message);
        }
        return value;
    }

    public static String notBlank(String value, String message) {
        notNull(value, message);
        if (value.isBlank()) {
            throw new ValidationException(message);
        }
        return value;
    }

    public static void check(boolean condition, String message) {
        if (!condition) {
            throw new ValidationException(message);
        }
    }
}
