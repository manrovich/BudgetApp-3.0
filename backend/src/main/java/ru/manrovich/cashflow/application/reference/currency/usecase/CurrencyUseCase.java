package ru.manrovich.cashflow.application.reference.currency.usecase;

import ru.manrovich.cashflow.application.reference.currency.usecase.command.SeedCurrenciesCommand;
import ru.manrovich.cashflow.application.reference.currency.usecase.result.SeedCurrenciesResult;

public interface CurrencyUseCase {
    SeedCurrenciesResult seed(SeedCurrenciesCommand command);
}
