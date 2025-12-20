package ru.manrovich.cashflow.application.reference.category.web.create;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateCategoryRequest(
        @NotBlank
        @Size(max = 64)
        String name
) {
}
