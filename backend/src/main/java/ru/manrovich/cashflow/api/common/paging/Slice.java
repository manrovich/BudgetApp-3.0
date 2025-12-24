package ru.manrovich.cashflow.api.common.paging;

import java.util.List;

public record Slice<T>(
        List<T> items,
        SlicePage page
) {}
