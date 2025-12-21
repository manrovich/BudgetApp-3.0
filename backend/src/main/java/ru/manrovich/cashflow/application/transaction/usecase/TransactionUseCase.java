package ru.manrovich.cashflow.application.transaction.usecase;

import ru.manrovich.cashflow.application.transaction.usecase.command.CreateTransactionCommand;
import ru.manrovich.cashflow.application.transaction.usecase.result.CreateTransactionResult;

public interface TransactionUseCase {
    CreateTransactionResult create(CreateTransactionCommand command);
}
