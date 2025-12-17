package ru.manrovich.cashflow.domain.budget.category;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository {

    Optional<Category> findById(CategoryId id);

    Optional<Category> findByName(String name);

    List<Category> findAll();

    Category save(Category category);

    void deleteById(CategoryId id);
}
