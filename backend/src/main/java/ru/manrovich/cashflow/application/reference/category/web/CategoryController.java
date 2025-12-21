package ru.manrovich.cashflow.application.reference.category.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.manrovich.cashflow.application.reference.category.usecase.CategoryUseCase;
import ru.manrovich.cashflow.application.reference.category.usecase.result.CreateCategoryResult;
import ru.manrovich.cashflow.application.reference.category.web.dto.CreateCategoryRequest;
import ru.manrovich.cashflow.application.reference.category.web.dto.CreateCategoryResponse;
import ru.manrovich.cashflow.application.reference.category.web.mapper.CategoryWebMapper;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryUseCase categoryUseCase;
    private final CategoryWebMapper webMapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CreateCategoryResponse create(@Valid @RequestBody CreateCategoryRequest request) {
        CreateCategoryResult result = categoryUseCase.create(webMapper.toCreateCommand(request));
        return webMapper.toCreateResponse(result);
    }
}
