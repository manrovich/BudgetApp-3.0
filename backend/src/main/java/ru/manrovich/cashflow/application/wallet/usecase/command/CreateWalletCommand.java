package ru.manrovich.cashflow.application.wallet.usecase.command;

public record CreateWalletCommand(String name, String currencyCode) {
}
