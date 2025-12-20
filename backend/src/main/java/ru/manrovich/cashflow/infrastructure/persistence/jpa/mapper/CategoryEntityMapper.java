package ru.manrovich.cashflow.infrastructure.persistence.jpa.mapper;

import org.springframework.stereotype.Component;
import ru.manrovich.cashflow.domain.kernel.id.CategoryId;
import ru.manrovich.cashflow.domain.kernel.id.UserId;
import ru.manrovich.cashflow.domain.reference.category.model.Category;
import ru.manrovich.cashflow.domain.reference.category.model.CategoryName;
import ru.manrovich.cashflow.infrastructure.persistence.jpa.entity.CategoryEntity;

@Component
public class CategoryEntityMapper implements EntityDomainMapper<CategoryEntity, Category> {

    public CategoryEntity toEntity(Category domain) {
        return new CategoryEntity(
                domain.id().value(),
                domain.ownerId().value(),
                domain.name().value()
        );
    }

    public Category toDomain(CategoryEntity entity) {
        return new Category(
                new CategoryId(entity.getId()),
                new UserId(entity.getUserId()),
                new CategoryName(entity.getName())
        );
    }
}
