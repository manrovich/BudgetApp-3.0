package ru.manrovich.cashflow.application.reference.currency.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.manrovich.cashflow.application.reference.currency.usecase.CurrencyUseCase;
import ru.manrovich.cashflow.application.reference.currency.usecase.command.SeedCurrenciesCommand;
import ru.manrovich.cashflow.application.reference.currency.usecase.query.ListCurrenciesQuery;
import ru.manrovich.cashflow.application.reference.currency.usecase.result.SeedCurrenciesResult;
import ru.manrovich.cashflow.domain.kernel.id.CurrencyId;
import ru.manrovich.cashflow.domain.reference.currency.model.Currency;
import ru.manrovich.cashflow.domain.reference.currency.port.CurrencyQueryPort;
import ru.manrovich.cashflow.domain.reference.currency.port.CurrencyRepository;
import ru.manrovich.cashflow.shared.query.Slice;
import ru.manrovich.cashflow.shared.readmodel.CurrencyListItem;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CurrencyApplicationService implements CurrencyUseCase {

    private final CurrencyRepository currencyRepository;
    private final CurrencyQueryPort currencyQueryPort;

    @Override
    @Transactional
    public SeedCurrenciesResult seed(SeedCurrenciesCommand command) {
        List<Currency> seed = defaultCurrencies();

        List<Currency> toInsert = new ArrayList<>();
        for (Currency currency : seed) {
            if (!currencyQueryPort.exists(currency.code())) {
                toInsert.add(currency);
            }
        }

        if (!command.dryRun() && !toInsert.isEmpty()) {
            currencyRepository.saveAll(toInsert);
        }

        int inserted = toInsert.size();
        int skipped = seed.size() - inserted;
        List<String> insertedCodes = toInsert.stream()
                .map(currency -> currency.code().value())
                .toList();

        return new SeedCurrenciesResult(inserted, skipped, command.dryRun(), insertedCodes);
    }

    @Override
    public Slice<CurrencyListItem> list(ListCurrenciesQuery query) {
        int page = query.page() == null ? 0 : Math.max(query.page(), 0);
        int size = query.size() == null ? 200 : Math.min(Math.max(query.size(), 1), 200);

        CurrencyQueryPort.CurrencySearchCriteria criteria =
                new CurrencyQueryPort.CurrencySearchCriteria(
                        query.query(),
                        page,
                        size);

        return currencyQueryPort.findListItems(criteria);
    }

    private static List<Currency> defaultCurrencies() {
        List<Currency> currencies = new ArrayList<>();
        for (java.util.Currency availableCurrency : java.util.Currency.getAvailableCurrencies()) {
            if (availableCurrency.getDefaultFractionDigits() == -1) {
                continue;
            }
            currencies.add(new Currency(
                    new CurrencyId(availableCurrency.getCurrencyCode()),
                    availableCurrency.getDisplayName(),
                    availableCurrency.getDefaultFractionDigits(),
                    availableCurrency.getSymbol()
            ));
        }
        return currencies;
    }
}
