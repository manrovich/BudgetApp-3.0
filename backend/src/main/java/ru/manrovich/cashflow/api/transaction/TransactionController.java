package ru.manrovich.cashflow.api.transaction;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.manrovich.cashflow.api.transaction.dto.CreateTransactionRequest;
import ru.manrovich.cashflow.api.transaction.dto.CreateTransactionResponse;
import ru.manrovich.cashflow.application.transaction.command.CreateTransactionCommand;
import ru.manrovich.cashflow.application.transaction.command.CreateTransactionResult;
import ru.manrovich.cashflow.application.transaction.command.TransactionCommandService;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionCommandService transactionCommandService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CreateTransactionResponse create(@Valid @RequestBody CreateTransactionRequest request) {
        CreateTransactionResult result = transactionCommandService.create(new CreateTransactionCommand(
                request.walletId(),
                request.categoryId(),
                request.type(),
                request.amount(),
                request.occurredAt()
        ));
        return new CreateTransactionResponse(result.id());
    }
}
