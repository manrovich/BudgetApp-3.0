package ru.manrovich.cashflow.infrastructure.persistence.jpa.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.manrovich.cashflow.domain.kernel.id.CurrencyId;
import ru.manrovich.cashflow.domain.reference.currency.port.CurrencyQueryPort;
import ru.manrovich.cashflow.infrastructure.persistence.jpa.repository.CurrencyJpaRepository;
import ru.manrovich.cashflow.infrastructure.persistence.jpa.repository.projection.CurrencyListRow;
import ru.manrovich.cashflow.shared.query.Slice;
import ru.manrovich.cashflow.shared.query.SlicePage;
import ru.manrovich.cashflow.shared.readmodel.CurrencyListItem;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CurrencyQueryPortAdapter implements CurrencyQueryPort {

    private final CurrencyJpaRepository jpaRepository;

    @Override
    @Transactional(readOnly = true)
    public boolean exists(CurrencyId code) {
        return jpaRepository.existsById(code.value());
    }

    @Override
    public Slice<CurrencyListItem> findListItems(CurrencySearchCriteria criteria) {
        int size = Math.min(Math.max(criteria.size(), 1), 200);
        int page = Math.max(criteria.page(), 0);

        PageRequest pageable = PageRequest.of(
                page,
                size + 1,
                Sort.by(Sort.Direction.ASC, "name")
                        .and(Sort.by(Sort.Direction.ASC, "code"))
        );

        List<CurrencyListRow> rows = jpaRepository.findListRows(criteria.query(), pageable);

        boolean hasNext = rows.size() > size;

        List<CurrencyListItem> items = rows.stream()
                .limit(size)
                .map(row -> new CurrencyListItem(
                        row.getCode(),
                        row.getName(),
                        row.getScale(),
                        row.getSymbol()
                ))
                .toList();

        return new Slice<>(items, new SlicePage(page, size, hasNext));
    }
}
