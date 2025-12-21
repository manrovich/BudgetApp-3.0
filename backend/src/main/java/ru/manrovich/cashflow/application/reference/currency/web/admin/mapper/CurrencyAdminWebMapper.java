package ru.manrovich.cashflow.application.reference.currency.web.admin.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import ru.manrovich.cashflow.application.reference.currency.usecase.command.SeedCurrenciesCommand;
import ru.manrovich.cashflow.application.reference.currency.usecase.result.SeedCurrenciesResult;
import ru.manrovich.cashflow.application.reference.currency.web.admin.dto.SeedCurrenciesRequest;
import ru.manrovich.cashflow.application.reference.currency.web.admin.dto.SeedCurrenciesResponse;

import java.util.Locale;

@Component
@RequiredArgsConstructor
public class CurrencyAdminWebMapper {

    private final MessageSource messageSource;

    public SeedCurrenciesCommand toSeedCommand(SeedCurrenciesRequest request) {
        SeedCurrenciesRequest req = request == null ? SeedCurrenciesRequest.defaultRequest() : request;
        return new SeedCurrenciesCommand(req.dryRunOrDefault());
    }

    public SeedCurrenciesResponse toSeedResponse(SeedCurrenciesResult result, Locale locale) {
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
