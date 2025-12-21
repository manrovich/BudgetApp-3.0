package ru.manrovich.cashflow.infrastructure.persistence.jpa.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.manrovich.cashflow.domain.kernel.id.CategoryId;
import ru.manrovich.cashflow.domain.kernel.id.UserId;
import ru.manrovich.cashflow.domain.reference.category.model.CategoryName;
import ru.manrovich.cashflow.domain.reference.category.port.CategoryQueryPort;
import ru.manrovich.cashflow.infrastructure.persistence.jpa.repository.CategoryJpaRepository;

@Component
@RequiredArgsConstructor
public class CategoryQueryPortAdapter implements CategoryQueryPort {

    private final CategoryJpaRepository jpaRepository;

    @Override
    public boolean exists(UserId ownerId, CategoryId categoryId) {
        return jpaRepository.existsByUserIdAndId(ownerId.value(), categoryId.value());
    }

    @Override
    public boolean existsByNameIgnoreCase(UserId ownerId, CategoryName name) {
        return jpaRepository.existsByUserIdAndNameIgnoreCase(ownerId.value(), name.value());
    }
}
