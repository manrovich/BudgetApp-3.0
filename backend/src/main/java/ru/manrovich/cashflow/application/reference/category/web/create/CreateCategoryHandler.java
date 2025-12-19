package ru.manrovich.cashflow.application.reference.category.web.create;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.manrovich.cashflow.application.reference.category.usecase.create.CreateCategoryCommand;
import ru.manrovich.cashflow.application.reference.category.usecase.create.CreateCategoryResult;
import ru.manrovich.cashflow.application.reference.category.usecase.create.CreateCategoryUseCase;

@Component
@RequiredArgsConstructor
public class CreateCategoryHandler {
    private final CreateCategoryUseCase useCase;

    public CreateCategoryResponse handle(CreateCategoryRequest request) {
        CreateCategoryResult result = useCase.execute(new CreateCategoryCommand(
                request.name()
        ));

        return new CreateCategoryResponse(
                result.categoryId(),
                result.name()
        );
    }
}
