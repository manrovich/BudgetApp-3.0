package ru.manrovich.cashflow.shared.query;

import java.util.List;

public record Slice<T>(
        List<T> items,
        SlicePage page
) {}
