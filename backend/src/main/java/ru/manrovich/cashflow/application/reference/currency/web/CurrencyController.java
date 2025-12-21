package ru.manrovich.cashflow.application.reference.currency.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.manrovich.cashflow.application.reference.currency.usecase.CurrencyUseCase;
import ru.manrovich.cashflow.application.reference.currency.web.dto.ListCurrenciesRequest;
import ru.manrovich.cashflow.application.reference.currency.web.mapper.CurrencyWebMapper;
import ru.manrovich.cashflow.shared.query.Slice;
import ru.manrovich.cashflow.shared.readmodel.CurrencyListItem;

@RestController
@RequestMapping("/api/currencies")
@RequiredArgsConstructor
public class CurrencyController {

    private final CurrencyUseCase useCase;
    private final CurrencyWebMapper mapper;

    @GetMapping
    public Slice<CurrencyListItem> list(@Valid @ModelAttribute ListCurrenciesRequest request) {
        return useCase.list(mapper.toListQuery(request));
    }
}