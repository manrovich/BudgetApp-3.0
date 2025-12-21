package ru.manrovich.cashflow.application.reference.category.usecase;

import ru.manrovich.cashflow.application.reference.category.usecase.command.CreateCategoryCommand;
import ru.manrovich.cashflow.application.reference.category.usecase.result.CreateCategoryResult;

public interface CategoryUseCase {
    CreateCategoryResult create(CreateCategoryCommand command);
}
