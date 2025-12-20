package ru.manrovich.cashflow.domain.reference.category.model;

import ru.manrovich.cashflow.domain.kernel.id.CategoryId;
import ru.manrovich.cashflow.domain.kernel.id.UserId;
import ru.manrovich.cashflow.domain.kernel.validation.DomainPreconditions;

public class Category {

    private final CategoryId id;
    private final UserId ownerId;
    private CategoryName name;

    public Category(CategoryId id, UserId ownerId, CategoryName name) {
        this.id = DomainPreconditions.notNull(id, "CategoryId must not be null");
        this.ownerId = DomainPreconditions.notNull(ownerId, "OwnerId must not be null");
        this.name = DomainPreconditions.notNull(name, "CategoryName must not be null");
    }

    public CategoryId id() {
        return id;
    }

    public UserId ownerId() {
        return ownerId;
    }

    public CategoryName name() {
        return name;
    }

    public void rename(CategoryName newName) {
        this.name = DomainPreconditions.notNull(newName, "CategoryName must not be null");
    }
}
