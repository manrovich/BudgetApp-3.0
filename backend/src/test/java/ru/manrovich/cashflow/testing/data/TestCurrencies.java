package ru.manrovich.cashflow.testing.data;

import ru.manrovich.cashflow.domain.kernel.id.CurrencyId;
import ru.manrovich.cashflow.domain.reference.currency.model.Currency;

public final class TestCurrencies {
    private TestCurrencies() {}

    public static Currency usd() {
        return new Currency(new CurrencyId("USD"), "US Dollar", 2, "$");
    }

    public static Currency rub() {
        return new Currency(new CurrencyId("RUB"), "Russian Ruble", 2, "₽");
    }
}
