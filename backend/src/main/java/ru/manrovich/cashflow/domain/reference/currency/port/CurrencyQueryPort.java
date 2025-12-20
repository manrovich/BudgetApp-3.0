package ru.manrovich.cashflow.domain.reference.currency.port;

import ru.manrovich.cashflow.domain.kernel.id.CurrencyId;

public interface CurrencyQueryPort {

    boolean exists(CurrencyId code);
}
