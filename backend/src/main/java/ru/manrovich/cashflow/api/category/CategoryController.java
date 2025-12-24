package ru.manrovich.cashflow.api.category;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.manrovich.cashflow.api.category.dto.CreateCategoryRequest;
import ru.manrovich.cashflow.api.category.dto.CreateCategoryResponse;
import ru.manrovich.cashflow.application.category.command.CreateCategoryCommand;
import ru.manrovich.cashflow.application.category.command.CreateCategoryResult;
import ru.manrovich.cashflow.application.category.command.CategoryCommandService;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryCommandService categoryCommandService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CreateCategoryResponse create(@Valid @RequestBody CreateCategoryRequest request) {
        CreateCategoryResult result = categoryCommandService.create(new CreateCategoryCommand(request.name()));
        return new CreateCategoryResponse(result.id(), result.name());
    }
}
