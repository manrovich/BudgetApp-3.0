package ru.manrovich.cashflow.application.transaction.usecase.create;

public interface CreateTransactionUseCase {
    CreateTransactionResult execute(CreateTransactionCommand command);
}
