package ru.manrovich.cashflow.infrastructure.budget;

import org.springframework.stereotype.Component;
import ru.manrovich.cashflow.domain.budget.category.Category;
import ru.manrovich.cashflow.domain.budget.category.CategoryId;

@Component
public class CategoryMapper {

    public Category toDomain(CategoryEntity entity) {
        if (entity == null) {
            return null;
        }
        return new Category(
                new CategoryId(entity.getId()),
                entity.getName()
        );
    }

    public CategoryEntity toEntity(Category domain) {
        if (domain == null) {
            return null;
        }
        CategoryEntity entity = new CategoryEntity();
        entity.setId(domain.id().value());
        entity.setName(domain.name());
        return entity;
    }
}
