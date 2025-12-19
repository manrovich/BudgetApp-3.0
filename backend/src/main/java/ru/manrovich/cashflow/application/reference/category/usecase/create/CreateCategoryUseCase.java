package ru.manrovich.cashflow.application.reference.category.usecase.create;

public interface CreateCategoryUseCase {
    CreateCategoryResult execute(CreateCategoryCommand command);
}
