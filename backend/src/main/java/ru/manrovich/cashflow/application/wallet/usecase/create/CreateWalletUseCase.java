package ru.manrovich.cashflow.application.wallet.usecase.create;

public interface CreateWalletUseCase {
    CreateWalletResult execute(CreateWalletCommand command);
}
