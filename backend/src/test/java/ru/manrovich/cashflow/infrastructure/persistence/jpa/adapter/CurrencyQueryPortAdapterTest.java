package ru.manrovich.cashflow.infrastructure.persistence.jpa.adapter;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.manrovich.cashflow.domain.reference.currency.port.CurrencyQueryPort;
import ru.manrovich.cashflow.infrastructure.persistence.jpa.repository.CurrencyJpaRepository;
import ru.manrovich.cashflow.infrastructure.persistence.jpa.repository.projection.CurrencyListRow;
import ru.manrovich.cashflow.shared.query.Slice;
import ru.manrovich.cashflow.shared.readmodel.CurrencyListItem;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CurrencyQueryPortAdapterTest {

    @Mock
    CurrencyJpaRepository repository;

    @Test
    void findListItems_shouldMapRowsToSlice_andComputeHasNext_andUseSortAndSizePlusOne() {
        CurrencyQueryPortAdapter adapter = new CurrencyQueryPortAdapter(repository);

        CurrencyListRow row = new TestRow(
                "RUB",
                "Russian Ruble",
                2,
                "₽"
        );

        List<CurrencyListRow> rows = new ArrayList<>();
        for (int i = 0; i < 201; i++) { // size+1
            rows.add(row);
        }

        when(repository.findListRows(eq("rub"), any(Pageable.class)))
                .thenReturn(rows);

        CurrencyQueryPort.CurrencySearchCriteria criteria =
                new CurrencyQueryPort.CurrencySearchCriteria("rub", 0, 200);

        Slice<CurrencyListItem> result = adapter.findListItems(criteria);

        assertNotNull(result);
        assertNotNull(result.page());
        assertEquals(0, result.page().number());
        assertEquals(200, result.page().size());
        assertTrue(result.page().hasNext());

        assertNotNull(result.items());
        assertEquals(200, result.items().size());

        CurrencyListItem item = result.items().get(0);
        assertEquals("RUB", item.code());
        assertEquals("Russian Ruble", item.name());
        assertEquals(2, item.scale());
        assertEquals("₽", item.symbol());

        ArgumentCaptor<Pageable> pageableCaptor =
                ArgumentCaptor.forClass(Pageable.class);

        verify(repository).findListRows(eq("rub"), pageableCaptor.capture());

        Pageable usedPageable = pageableCaptor.getValue();
        assertEquals(0, usedPageable.getPageNumber());
        assertEquals(201, usedPageable.getPageSize()); // size+1

        Sort usedSort = usedPageable.getSort();

        Sort.Order codeOrder = usedSort.getOrderFor("code");
        assertNotNull(codeOrder);
        assertEquals(Sort.Direction.ASC, codeOrder.getDirection());

        Sort.Order nameOrder = usedSort.getOrderFor("name");
        assertNotNull(nameOrder);
        assertEquals(Sort.Direction.ASC, nameOrder.getDirection());
    }

    @Test
    void findListItems_shouldPassNullQueryToRepository() {
        CurrencyQueryPortAdapter adapter = new CurrencyQueryPortAdapter(repository);

        when(repository.findListRows(isNull(), any(Pageable.class)))
                .thenReturn(List.of());

        CurrencyQueryPort.CurrencySearchCriteria criteria =
                new CurrencyQueryPort.CurrencySearchCriteria(null, 0, 200);

        Slice<CurrencyListItem> result = adapter.findListItems(criteria);

        assertNotNull(result);
        verify(repository).findListRows(isNull(), any(Pageable.class));
    }

    @Test
    void findListItems_shouldMapNullSymbol() {
        CurrencyQueryPortAdapter adapter = new CurrencyQueryPortAdapter(repository);

        CurrencyListRow row = new TestRow(
                "USD",
                "US Dollar",
                2,
                null
        );

        when(repository.findListRows(isNull(), any(Pageable.class)))
                .thenReturn(List.of(row));

        CurrencyQueryPort.CurrencySearchCriteria criteria =
                new CurrencyQueryPort.CurrencySearchCriteria(null, 0, 200);

        Slice<CurrencyListItem> result = adapter.findListItems(criteria);

        assertNotNull(result);
        assertNotNull(result.items());
        assertEquals(1, result.items().size());

        CurrencyListItem item = result.items().get(0);
        assertEquals("USD", item.code());
        assertEquals("US Dollar", item.name());
        assertEquals(2, item.scale());
        assertNull(item.symbol());
    }

    private static class TestRow implements CurrencyListRow {

        private final String code;
        private final String name;
        private final int scale;
        private final String symbol;

        private TestRow(
                String code,
                String name,
                int scale,
                String symbol
        ) {
            this.code = code;
            this.name = name;
            this.scale = scale;
            this.symbol = symbol;
        }

        @Override
        public String getCode() {
            return code;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public int getScale() {
            return scale;
        }

        @Override
        public String getSymbol() {
            return symbol;
        }
    }
}
