package ru.manrovich.cashflow.application.transaction.web.create;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.manrovich.cashflow.application.transaction.usecase.create.CreateTransactionCommand;
import ru.manrovich.cashflow.application.transaction.usecase.create.CreateTransactionResult;
import ru.manrovich.cashflow.application.transaction.usecase.create.CreateTransactionUseCase;

@Component
@RequiredArgsConstructor
public class CreateTransactionHandler {

    private final CreateTransactionUseCase useCase;

    public CreateTransactionResponse handle(CreateTransactionRequest request) {
        CreateTransactionCommand command = new CreateTransactionCommand(
                request.walletId(),
                request.categoryId(),
                request.type(),
                request.amount(),
                request.occurredAt()
        );

        CreateTransactionResult result = useCase.execute(command);

        return new CreateTransactionResponse(result.id());
    }
}
