package ru.manrovich.cashflow.application.reference.category.web.dto;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record ListCategoriesRequest(
        @Nullable
        String query,
        @Nullable
        @Min(0)
        Integer page,
        @Nullable
        @Min(1)
        @Max(200)
        Integer size
) {
}
