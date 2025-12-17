package ru.manrovich.cashflow.infrastructure.budget;

import org.springframework.stereotype.Repository;
import ru.manrovich.cashflow.domain.budget.category.Category;
import ru.manrovich.cashflow.domain.budget.category.CategoryId;
import ru.manrovich.cashflow.domain.budget.category.CategoryRepository;

import java.util.List;
import java.util.Optional;

@Repository
public class CategoryRepositoryImpl implements CategoryRepository {

    private final JpaCategoryRepository jpaRepository;
    private final CategoryMapper mapper;

    public CategoryRepositoryImpl(JpaCategoryRepository jpaRepository, CategoryMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Optional<Category> findById(CategoryId id) {
        return jpaRepository.findById(id.value())
                .map(mapper::toDomain);
    }

    @Override
    public Optional<Category> findByName(String name) {
        return jpaRepository.findByName(name)
                .map(mapper::toDomain);
    }

    @Override
    public List<Category> findAll() {
        return jpaRepository.findAll().stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public Category save(Category category) {
        CategoryEntity saved = jpaRepository.save(mapper.toEntity(category));
        return mapper.toDomain(saved);
    }

    @Override
    public void deleteById(CategoryId id) {
        jpaRepository.deleteById(id.value());
    }
}
