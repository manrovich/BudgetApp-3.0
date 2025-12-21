package ru.manrovich.cashflow.application.reference.category.usecase;

import ru.manrovich.cashflow.application.reference.category.usecase.command.CreateCategoryCommand;
import ru.manrovich.cashflow.application.reference.category.usecase.query.ListCategoriesQuery;
import ru.manrovich.cashflow.application.reference.category.usecase.result.CreateCategoryResult;
import ru.manrovich.cashflow.shared.query.Slice;
import ru.manrovich.cashflow.shared.readmodel.CategoryListItem;

public interface CategoryUseCase {
    CreateCategoryResult create(CreateCategoryCommand command);
    Slice<CategoryListItem> list(ListCategoriesQuery query);
}
