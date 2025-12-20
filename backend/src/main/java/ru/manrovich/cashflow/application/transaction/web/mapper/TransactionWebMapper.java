package ru.manrovich.cashflow.application.transaction.web.mapper;

import org.springframework.stereotype.Component;
import ru.manrovich.cashflow.application.transaction.usecase.command.CreateTransactionCommand;
import ru.manrovich.cashflow.application.transaction.usecase.result.CreateTransactionResult;
import ru.manrovich.cashflow.application.transaction.web.dto.CreateTransactionRequest;
import ru.manrovich.cashflow.application.transaction.web.dto.CreateTransactionResponse;

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
}
