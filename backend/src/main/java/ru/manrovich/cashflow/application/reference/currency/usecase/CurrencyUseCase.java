package ru.manrovich.cashflow.application.reference.currency.usecase;

import ru.manrovich.cashflow.application.reference.currency.usecase.command.SeedCurrenciesCommand;
import ru.manrovich.cashflow.application.reference.currency.usecase.query.ListCurrenciesQuery;
import ru.manrovich.cashflow.application.reference.currency.usecase.result.SeedCurrenciesResult;
import ru.manrovich.cashflow.shared.query.Slice;
import ru.manrovich.cashflow.shared.readmodel.CurrencyListItem;

public interface CurrencyUseCase {
    SeedCurrenciesResult seed(SeedCurrenciesCommand command);
    Slice<CurrencyListItem> list(ListCurrenciesQuery query);
}
