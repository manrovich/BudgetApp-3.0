package ru.manrovich.cashflow.application.wallet.web.dto;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record ListWalletsRequest(
        @Nullable
        @Min(0)
        Integer page,
        @Nullable
        @Min(1)
        @Max(200)
        Integer size
) {}
