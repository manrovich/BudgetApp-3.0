package ru.manrovich.cashflow.application.reference.category.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.manrovich.cashflow.application.reference.category.usecase.CategoryUseCase;
import ru.manrovich.cashflow.application.reference.category.usecase.query.ListCategoriesQuery;
import ru.manrovich.cashflow.application.reference.category.usecase.result.CreateCategoryResult;
import ru.manrovich.cashflow.application.reference.category.web.dto.CreateCategoryRequest;
import ru.manrovich.cashflow.application.reference.category.web.dto.CreateCategoryResponse;
import ru.manrovich.cashflow.application.reference.category.web.dto.ListCategoriesRequest;
import ru.manrovich.cashflow.application.reference.category.web.mapper.CategoryWebMapper;
import ru.manrovich.cashflow.shared.query.Slice;
import ru.manrovich.cashflow.shared.readmodel.CategoryListItem;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryUseCase useCase;
    private final CategoryWebMapper mapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CreateCategoryResponse create(@Valid @RequestBody CreateCategoryRequest request) {
        CreateCategoryResult result = useCase.create(mapper.toCreateCommand(request));
        return mapper.toCreateResponse(result);
    }

    @GetMapping
    public Slice<CategoryListItem> list(@Valid @ModelAttribute ListCategoriesRequest request) {
        ListCategoriesQuery query = mapper.toListQuery(request);
        return useCase.list(query);
    }
}
