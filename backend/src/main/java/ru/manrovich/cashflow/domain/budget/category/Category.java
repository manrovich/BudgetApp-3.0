package ru.manrovich.cashflow.domain.budget.category;

public record Category(CategoryId id, String name) {

    public Category {
        if (id == null) {
            throw new IllegalArgumentException("Category id must not be null");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Category name must not be blank");
        }
    }

    public Category rename(String newName) {
        return new Category(this.id, newName);
    }
}