package ru.manrovich.cashflow.application.reference.currency.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.manrovich.cashflow.application.reference.currency.usecase.CurrencyUseCase;
import ru.manrovich.cashflow.application.reference.currency.usecase.result.SeedCurrenciesResult;
import ru.manrovich.cashflow.application.reference.currency.web.dto.SeedCurrenciesRequest;
import ru.manrovich.cashflow.application.reference.currency.web.dto.SeedCurrenciesResponse;
import ru.manrovich.cashflow.application.reference.currency.web.mapper.CurrencyWebMapper;

import java.util.Locale;

@RestController
@RequestMapping("/api/admin/currencies")
@RequiredArgsConstructor
public class CurrencyAdminController {

    private final CurrencyUseCase currencyUseCase;
    private final CurrencyWebMapper webMapper;

    @PostMapping("seed")
    public SeedCurrenciesResponse seed(
            @Valid @RequestBody(required = false) SeedCurrenciesRequest request,
            Locale locale
    ) {
        SeedCurrenciesResult result = currencyUseCase.seed(webMapper.toSeedCommand(request));
        return webMapper.toSeedResponse(result, locale);
    }
}
