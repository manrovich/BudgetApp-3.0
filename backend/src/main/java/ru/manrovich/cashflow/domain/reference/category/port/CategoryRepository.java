package ru.manrovich.cashflow.domain.reference.category.port;

import ru.manrovich.cashflow.domain.kernel.id.CategoryId;
import ru.manrovich.cashflow.domain.kernel.id.UserId;
import ru.manrovich.cashflow.domain.reference.category.model.Category;

import java.util.Optional;

public interface CategoryRepository {
    Category save(Category category);

    Optional<Category> findById(UserId ownerId, CategoryId id);

    void deleteById(UserId ownerId, CategoryId id);
}
