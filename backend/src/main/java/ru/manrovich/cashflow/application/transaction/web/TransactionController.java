package ru.manrovich.cashflow.application.transaction.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.manrovich.cashflow.application.transaction.usecase.TransactionUseCase;
import ru.manrovich.cashflow.application.transaction.usecase.result.CreateTransactionResult;
import ru.manrovich.cashflow.application.transaction.web.dto.CreateTransactionRequest;
import ru.manrovich.cashflow.application.transaction.web.dto.CreateTransactionResponse;
import ru.manrovich.cashflow.application.transaction.web.mapper.TransactionWebMapper;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionUseCase useCase;
    private final TransactionWebMapper mapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CreateTransactionResponse create(@Valid @RequestBody CreateTransactionRequest request) {
        CreateTransactionResult result = useCase.create(mapper.toCreateCommand(request));
        return mapper.toCreateResponse(result);
    }
}
