package ru.manrovich.cashflow.application.reference.category.web;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.manrovich.cashflow.application.reference.category.web.create.CreateCategoryHandler;
import ru.manrovich.cashflow.application.reference.category.web.create.CreateCategoryRequest;
import ru.manrovich.cashflow.application.reference.category.web.create.CreateCategoryResponse;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CreateCategoryHandler createCategoryHandler;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CreateCategoryResponse create(@RequestBody CreateCategoryRequest request) {
        return createCategoryHandler.handle(request);
    }
}