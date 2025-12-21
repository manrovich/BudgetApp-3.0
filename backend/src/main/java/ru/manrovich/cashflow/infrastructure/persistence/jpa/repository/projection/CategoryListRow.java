package ru.manrovich.cashflow.infrastructure.persistence.jpa.repository.projection;

import java.util.UUID;

public interface CategoryListRow {
    UUID getId();
    String getName();
}
