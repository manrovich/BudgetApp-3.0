package ru.manrovich.cashflow.application.reference.currency.web.dto;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

public record ListCurrenciesRequest(
        @Nullable
        @Size(max = 64)
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