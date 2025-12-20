package ru.manrovich.cashflow.application.reference.currency.web.admin.seed;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import ru.manrovich.cashflow.application.reference.currency.usecase.seed.SeedCurrenciesCommand;
import ru.manrovich.cashflow.application.reference.currency.usecase.seed.SeedCurrenciesResult;
import ru.manrovich.cashflow.application.reference.currency.usecase.seed.SeedCurrenciesUseCase;

import java.util.Locale;

@Component
@RequiredArgsConstructor
public class SeedCurrenciesHandler {
    private final SeedCurrenciesUseCase useCase;
    private final MessageSource messageSource;

    public SeedCurrenciesResponse handle(SeedCurrenciesRequest request, Locale locale) {
        SeedCurrenciesRequest req = request == null
                ? SeedCurrenciesRequest.defaultRequest()
                : request;

        SeedCurrenciesResult result = useCase.execute(new SeedCurrenciesCommand(req.dryRun()));

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
