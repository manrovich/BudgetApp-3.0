package ru.manrovich.cashflow.domain.wallet.port;

import ru.manrovich.cashflow.domain.kernel.id.CurrencyId;
import ru.manrovich.cashflow.domain.kernel.id.UserId;
import ru.manrovich.cashflow.domain.kernel.id.WalletId;
import ru.manrovich.cashflow.shared.query.Slice;
import ru.manrovich.cashflow.shared.readmodel.WalletListItem;

public interface WalletQueryPort {

    boolean exists(UserId ownerId, WalletId walletId);

    CurrencyId getCurrencyId(UserId ownerId, WalletId walletId);

    Slice<WalletListItem> findListItems(UserId ownerId, WalletSearchCriteria criteria);

    record WalletSearchCriteria(
            int page,
            int size
    ) {}
}
