package ru.manrovich.cashflow.application.reference.currency.usecase.seed;

public interface SeedCurrenciesUseCase {
    SeedCurrenciesResult execute(SeedCurrenciesCommand command);
}
