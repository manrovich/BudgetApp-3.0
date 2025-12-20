package ru.manrovich.cashflow.application.transaction.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.manrovich.cashflow.application.transaction.web.create.CreateTransactionHandler;
import ru.manrovich.cashflow.application.transaction.web.create.CreateTransactionRequest;
import ru.manrovich.cashflow.application.transaction.web.create.CreateTransactionResponse;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final CreateTransactionHandler createTransactionHandler;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CreateTransactionResponse create(@Valid @RequestBody CreateTransactionRequest request) {
        return createTransactionHandler.handle(request);
    }
}
