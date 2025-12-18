package ru.manrovich.cashflow.application.reference.currency.web.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.manrovich.cashflow.application.reference.currency.web.admin.seed.SeedCurrenciesHandler;
import ru.manrovich.cashflow.application.reference.currency.web.admin.seed.SeedCurrenciesRequest;
import ru.manrovich.cashflow.application.reference.currency.web.admin.seed.SeedCurrenciesResponse;

import java.util.Locale;

@RestController
@RequestMapping("/api/admin/currencies")
@RequiredArgsConstructor
public class CurrencyAdminController {

    private final SeedCurrenciesHandler seedCurrenciesHandler;

    @PostMapping("seed")
    public SeedCurrenciesResponse seed(
            @RequestBody(required = false) SeedCurrenciesRequest request,
            Locale locale
    ) {
        return seedCurrenciesHandler.handle(request, locale);
    }
}
