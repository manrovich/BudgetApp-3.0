package ru.manrovich.cashflow.domain.reference.category.port;

import ru.manrovich.cashflow.domain.kernel.id.CategoryId;
import ru.manrovich.cashflow.domain.kernel.id.UserId;
import ru.manrovich.cashflow.domain.reference.category.model.CategoryName;

public interface CategoryQueryPort {
    boolean exists(UserId ownerId, CategoryId categoryId);

    boolean existsByNameIgnoreCase(UserId ownerId, CategoryName name);
}
