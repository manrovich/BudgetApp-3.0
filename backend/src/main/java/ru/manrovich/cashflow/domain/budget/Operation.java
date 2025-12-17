package ru.manrovich.cashflow.domain.budget;

import ru.manrovich.cashflow.domain.budget.category.Category;
import ru.manrovich.cashflow.domain.ledger.transaction.Transaction;

public class Operation {
    Transaction transaction;
    Category category;
}