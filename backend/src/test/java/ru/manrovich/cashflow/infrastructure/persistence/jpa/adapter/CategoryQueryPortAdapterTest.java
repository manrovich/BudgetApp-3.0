package ru.manrovich.cashflow.infrastructure.persistence.jpa.adapter;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.manrovich.cashflow.domain.reference.category.port.CategoryQueryPort;
import ru.manrovich.cashflow.infrastructure.persistence.jpa.repository.CategoryJpaRepository;
import ru.manrovich.cashflow.infrastructure.persistence.jpa.repository.projection.CategoryListRow;
import ru.manrovich.cashflow.shared.query.Slice;
import ru.manrovich.cashflow.shared.readmodel.CategoryListItem;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static ru.manrovich.cashflow.testing.data.TestUsers.USER_1;

@ExtendWith(MockitoExtension.class)
class CategoryQueryPortAdapterTest {

    @Mock
    CategoryJpaRepository repository;

    @Test
    void findListItems_shouldMapRowsToSlice_andComputeHasNext_andUseSortAndSizePlusOne() {
        CategoryQueryPortAdapter adapter = new CategoryQueryPortAdapter(repository);

        UUID categoryId = UUID.randomUUID();

        CategoryListRow row = new TestRow(categoryId, "Food");

        List<CategoryListRow> rows = new ArrayList<>();
        for (int i = 0; i < 201; i++) { // size+1
            rows.add(row);
        }

        when(repository.findListRows(eq(USER_1.value()), eq("food"), any(Pageable.class)))
                .thenReturn(rows);

        CategoryQueryPort.CategorySearchCriteria criteria =
                new CategoryQueryPort.CategorySearchCriteria(USER_1, "food", 0, 200);

        Slice<CategoryListItem> result = adapter.findListItems(criteria);

        assertNotNull(result);
        assertNotNull(result.page());
        assertEquals(0, result.page().number());
        assertEquals(200, result.page().size());
        assertTrue(result.page().hasNext());

        assertNotNull(result.items());
        assertEquals(200, result.items().size());

        CategoryListItem item = result.items().get(0);
        assertEquals(categoryId, item.id());
        assertEquals("Food", item.name());

        ArgumentCaptor<Pageable> pageableCaptor =
                ArgumentCaptor.forClass(Pageable.class);

        verify(repository).findListRows(eq(USER_1.value()), eq("food"), pageableCaptor.capture());

        Pageable usedPageable = pageableCaptor.getValue();
        assertEquals(0, usedPageable.getPageNumber());
        assertEquals(201, usedPageable.getPageSize()); // size+1

        Sort usedSort = usedPageable.getSort();

        Sort.Order nameOrder = usedSort.getOrderFor("name");
        assertNotNull(nameOrder);
        assertEquals(Sort.Direction.ASC, nameOrder.getDirection());

        Sort.Order idOrder = usedSort.getOrderFor("id");
        assertNotNull(idOrder);
        assertEquals(Sort.Direction.ASC, idOrder.getDirection());
    }

    @Test
    void findListItems_shouldPassNullQueryToRepository() {
        CategoryQueryPortAdapter adapter = new CategoryQueryPortAdapter(repository);

        when(repository.findListRows(eq(USER_1.value()), isNull(), any(Pageable.class)))
                .thenReturn(List.of());

        CategoryQueryPort.CategorySearchCriteria criteria =
                new CategoryQueryPort.CategorySearchCriteria(USER_1, null, 0, 200);

        Slice<CategoryListItem> result = adapter.findListItems(criteria);

        assertNotNull(result);
        verify(repository).findListRows(eq(USER_1.value()), isNull(), any(Pageable.class));
    }

    private static class TestRow implements CategoryListRow {

        private final UUID id;
        private final String name;

        private TestRow(UUID id, String name) {
            this.id = id;
            this.name = name;
        }

        @Override
        public UUID getId() {
            return id;
        }

        @Override
        public String getName() {
            return name;
        }
    }
}
