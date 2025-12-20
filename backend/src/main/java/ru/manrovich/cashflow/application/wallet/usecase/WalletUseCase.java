package ru.manrovich.cashflow.application.wallet.usecase;

import ru.manrovich.cashflow.application.wallet.usecase.command.CreateWalletCommand;
import ru.manrovich.cashflow.application.wallet.usecase.result.CreateWalletResult;

public interface WalletUseCase {
    CreateWalletResult create(CreateWalletCommand command);
}
