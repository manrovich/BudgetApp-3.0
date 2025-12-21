package ru.manrovich.cashflow.domain.reference.category.port;

import ru.manrovich.cashflow.domain.kernel.id.CategoryId;
import ru.manrovich.cashflow.domain.kernel.id.UserId;
import ru.manrovich.cashflow.domain.reference.category.model.CategoryName;
import ru.manrovich.cashflow.shared.query.Slice;
import ru.manrovich.cashflow.shared.readmodel.CategoryListItem;

public interface CategoryQueryPort {
    boolean exists(UserId ownerId, CategoryId categoryId);

    boolean existsByNameIgnoreCase(UserId ownerId, CategoryName name);

    Slice<CategoryListItem> findListItems(CategorySearchCriteria criteria);

    record CategorySearchCriteria(
            UserId ownerId,
            String query,
            int page,
            int size
    ) {
    }
}
