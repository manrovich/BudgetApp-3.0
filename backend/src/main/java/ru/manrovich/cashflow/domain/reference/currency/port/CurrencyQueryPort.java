package ru.manrovich.cashflow.domain.reference.currency.port;

import ru.manrovich.cashflow.domain.kernel.id.CurrencyId;
import ru.manrovich.cashflow.shared.query.Slice;
import ru.manrovich.cashflow.shared.readmodel.CurrencyListItem;

public interface CurrencyQueryPort {

    boolean exists(CurrencyId code);

    Slice<CurrencyListItem> findListItems(CurrencySearchCriteria criteria);

    record CurrencySearchCriteria(
            String query,
            int page,
            int size
    ) {
    }
}
