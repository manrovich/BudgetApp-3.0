package ru.manrovich.cashflow.api.common.paging;

import java.util.List;

public record ItemsResponse<T>(List<T> items){

    public static <T> ItemsResponse<T> of(List<T> items) {
        return new ItemsResponse<>(items);
    }
}
