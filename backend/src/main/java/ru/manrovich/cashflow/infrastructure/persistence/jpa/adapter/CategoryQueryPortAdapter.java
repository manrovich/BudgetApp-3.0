package ru.manrovich.cashflow.infrastructure.persistence.jpa.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import ru.manrovich.cashflow.domain.kernel.id.CategoryId;
import ru.manrovich.cashflow.domain.kernel.id.UserId;
import ru.manrovich.cashflow.domain.reference.category.model.CategoryName;
import ru.manrovich.cashflow.domain.reference.category.port.CategoryQueryPort;
import ru.manrovich.cashflow.infrastructure.persistence.jpa.repository.CategoryJpaRepository;
import ru.manrovich.cashflow.infrastructure.persistence.jpa.repository.projection.CategoryListRow;
import ru.manrovich.cashflow.shared.query.Slice;
import ru.manrovich.cashflow.shared.query.SlicePage;
import ru.manrovich.cashflow.shared.readmodel.CategoryListItem;

import java.util.List;
import java.util.UUID;

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

    @Override
    public Slice<CategoryListItem> findListItems(CategorySearchCriteria criteria) {
        int size = Math.min(Math.max(criteria.size(), 1), 200);
        int page = Math.max(criteria.page(), 0);

        PageRequest pageable = PageRequest.of(
                page,
                size + 1,
                Sort.by(Sort.Direction.ASC, "name")
                        .and(Sort.by(Sort.Direction.ASC, "id"))
        );

        UUID ownerId = criteria.ownerId().value();

        List<CategoryListRow> rows = jpaRepository.findListRows(ownerId, criteria.query(), pageable);

        boolean hasNext = rows.size() > size;

        List<CategoryListItem> items = rows.stream()
                .limit(size)
                .map(row -> new CategoryListItem(row.getId(), row.getName()))
                .toList();

        return new Slice<>(items, new SlicePage(page, size, hasNext));
    }
}
