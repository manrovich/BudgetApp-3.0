package ru.manrovich.cashflow.application.reference.currency.usecase.seed;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.manrovich.cashflow.domain.kernel.id.CurrencyId;
import ru.manrovich.cashflow.domain.reference.currency.model.Currency;
import ru.manrovich.cashflow.domain.reference.currency.port.CurrencyQueryPort;
import ru.manrovich.cashflow.domain.reference.currency.port.CurrencyRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SeedCurrenciesService implements SeedCurrenciesUseCase {

    private final CurrencyRepository currencyRepository;
    private final CurrencyQueryPort currencyQueryPort;

    @Override
    @Transactional
    public SeedCurrenciesResult execute(SeedCurrenciesCommand command) {
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