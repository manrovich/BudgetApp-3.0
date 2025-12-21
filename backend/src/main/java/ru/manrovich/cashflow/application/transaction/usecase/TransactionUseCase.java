package ru.manrovich.cashflow.application.transaction.usecase;

import ru.manrovich.cashflow.application.transaction.usecase.command.CreateTransactionCommand;
import ru.manrovich.cashflow.application.transaction.usecase.query.ListTransactionsQuery;
import ru.manrovich.cashflow.application.transaction.usecase.result.CreateTransactionResult;
import ru.manrovich.cashflow.shared.query.Slice;
import ru.manrovich.cashflow.shared.readmodel.TransactionListItem;

public interface TransactionUseCase {
    CreateTransactionResult create(CreateTransactionCommand command);
    Slice<TransactionListItem> list(ListTransactionsQuery query);
}
