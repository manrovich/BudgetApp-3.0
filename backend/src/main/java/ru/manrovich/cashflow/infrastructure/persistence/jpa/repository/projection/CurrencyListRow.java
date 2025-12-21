package ru.manrovich.cashflow.infrastructure.persistence.jpa.repository.projection;

public interface CurrencyListRow {

    String getCode();

    String getName();

    int getScale();

    String getSymbol();
}