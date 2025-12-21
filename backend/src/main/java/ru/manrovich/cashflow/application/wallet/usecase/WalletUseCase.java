package ru.manrovich.cashflow.application.wallet.usecase;

import ru.manrovich.cashflow.application.wallet.usecase.command.CreateWalletCommand;
import ru.manrovich.cashflow.application.wallet.usecase.query.ListWalletsQuery;
import ru.manrovich.cashflow.application.wallet.usecase.result.CreateWalletResult;
import ru.manrovich.cashflow.shared.query.Slice;
import ru.manrovich.cashflow.shared.readmodel.WalletListItem;

public interface WalletUseCase {
    CreateWalletResult create(CreateWalletCommand command);
    Slice<WalletListItem> list(ListWalletsQuery query);
}
