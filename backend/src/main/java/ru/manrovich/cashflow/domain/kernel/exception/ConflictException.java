package ru.manrovich.cashflow.domain.kernel.exception;

public class ConflictException extends DomainException {
    public ConflictException(String message) {
        super(message);
    }
}
