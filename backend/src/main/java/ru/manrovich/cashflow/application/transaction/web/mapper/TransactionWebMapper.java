package ru.manrovich.cashflow.application.transaction.web.mapper;

import org.springframework.stereotype.Component;
import ru.manrovich.cashflow.application.transaction.usecase.command.CreateTransactionCommand;
import ru.manrovich.cashflow.application.transaction.usecase.query.ListTransactionsQuery;
import ru.manrovich.cashflow.application.transaction.usecase.result.CreateTransactionResult;
import ru.manrovich.cashflow.application.transaction.web.dto.CreateTransactionRequest;
import ru.manrovich.cashflow.application.transaction.web.dto.CreateTransactionResponse;
import ru.manrovich.cashflow.application.transaction.web.dto.ListTransactionsRequest;

@Component
public class TransactionWebMapper {

    public CreateTransactionCommand toCreateCommand(CreateTransactionRequest request) {
        return new CreateTransactionCommand(
                request.walletId(),
                request.categoryId(),
                request.type(),
                request.amount(),
                request.occurredAt()
        );
    }

    public CreateTransactionResponse toCreateResponse(CreateTransactionResult result) {
        return new CreateTransactionResponse(result.id());
    }

    public ListTransactionsQuery toListQuery(ListTransactionsRequest request) {
        return new ListTransactionsQuery(
                request.walletId(),
                request.from(),
                request.to(),
                request.page(),
                request.size()
        );
    }
}
