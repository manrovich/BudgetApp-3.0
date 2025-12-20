package ru.manrovich.cashflow.infrastructure.persistence.jpa.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.manrovich.cashflow.domain.kernel.id.CategoryId;
import ru.manrovich.cashflow.domain.kernel.id.UserId;
import ru.manrovich.cashflow.domain.reference.category.model.Category;
import ru.manrovich.cashflow.domain.reference.category.port.CategoryRepository;
import ru.manrovich.cashflow.infrastructure.persistence.jpa.mapper.CategoryEntityMapper;
import ru.manrovich.cashflow.infrastructure.persistence.jpa.repository.CategoryJpaRepository;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CategoryRepositoryAdapter implements CategoryRepository {

    private final CategoryJpaRepository jpaRepository;
    private final CategoryEntityMapper mapper;

    @Override
    public Category save(Category category) {
        var saved = jpaRepository.save(mapper.toEntity(category));
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<Category> findById(UserId ownerId, CategoryId id) {
        return jpaRepository.findByUserIdAndId(ownerId.value(), id.value()).map(mapper::toDomain);
    }

    @Override
    public void deleteById(UserId ownerId, CategoryId id) {
        jpaRepository.deleteByUserIdAndId(ownerId.value(), id.value());
    }
}
