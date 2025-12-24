package ru.manrovich.cashflow.api.currency.admin;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.manrovich.cashflow.api.currency.admin.dto.SeedCurrenciesRequest;
import ru.manrovich.cashflow.api.currency.admin.dto.SeedCurrenciesResponse;
import ru.manrovich.cashflow.application.currency.command.SeedCurrenciesCommand;
import ru.manrovich.cashflow.application.currency.command.SeedCurrenciesResult;
import ru.manrovich.cashflow.application.currency.command.CurrencyCommandService;

import java.util.Locale;

@RestController
@RequestMapping("/api/admin/currencies")
@RequiredArgsConstructor
public class CurrencyAdminController {

    private final CurrencyCommandService currencyCommandService;
    private final MessageSource messageSource;

    @PostMapping("seed")
    public SeedCurrenciesResponse seed(
            @Valid @RequestBody(required = false) SeedCurrenciesRequest request,
            Locale locale
    ) {
        SeedCurrenciesRequest actualRequest = request == null
                ? SeedCurrenciesRequest.defaultRequest()
                : request;

        SeedCurrenciesResult result = currencyCommandService.seed(new SeedCurrenciesCommand(
                actualRequest.dryRunOrDefault()
        ));

        String summary = messageSource.getMessage(
                "currency.seed.summary",
                new Object[]{result.inserted(), result.skipped()},
                locale
        );

        return new SeedCurrenciesResponse(
                result.inserted(),
                result.skipped(),
                result.dryRun(),
                result.insertedCodes(),
                summary
        );
    }
}
