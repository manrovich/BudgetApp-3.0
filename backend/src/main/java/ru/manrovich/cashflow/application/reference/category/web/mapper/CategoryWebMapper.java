package ru.manrovich.cashflow.application.reference.category.web.mapper;

import org.springframework.stereotype.Component;
import ru.manrovich.cashflow.application.reference.category.usecase.command.CreateCategoryCommand;
import ru.manrovich.cashflow.application.reference.category.usecase.query.ListCategoriesQuery;
import ru.manrovich.cashflow.application.reference.category.usecase.result.CreateCategoryResult;
import ru.manrovich.cashflow.application.reference.category.web.dto.CreateCategoryRequest;
import ru.manrovich.cashflow.application.reference.category.web.dto.CreateCategoryResponse;
import ru.manrovich.cashflow.application.reference.category.web.dto.ListCategoriesRequest;

@Component
public class CategoryWebMapper {

    public CreateCategoryCommand toCreateCommand(CreateCategoryRequest request) {
        return new CreateCategoryCommand(request.name());
    }

    public CreateCategoryResponse toCreateResponse(CreateCategoryResult result) {
        return new CreateCategoryResponse(result.categoryId(), result.name());
    }

    public ListCategoriesQuery toListQuery(ListCategoriesRequest request) {
        return new ListCategoriesQuery(
                request.query(),
                request.page(),
                request.size()
        );
    }
}
