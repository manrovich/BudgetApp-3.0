package ru.manrovich.cashflow.application.wallet.usecase;

import ru.manrovich.cashflow.application.wallet.usecase.command.CreateWalletCommand;
import ru.manrovich.cashflow.application.wallet.usecase.query.ListWalletTransactionsQuery;
import ru.manrovich.cashflow.application.wallet.usecase.result.CreateWalletResult;
import ru.manrovich.cashflow.shared.readmodel.TransactionListItem;

import java.util.List;

public interface WalletUseCase {
    CreateWalletResult create(CreateWalletCommand command);
    List<TransactionListItem> listWalletTransactions(ListWalletTransactionsQuery query);
}
