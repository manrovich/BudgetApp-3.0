package ru.manrovich.cashflow.api.common.paging;

public record SlicePage(
        int number,
        int size,
        boolean hasNext
) {}
