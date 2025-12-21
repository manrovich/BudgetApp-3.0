package ru.manrovich.cashflow.infrastructure.persistence.jpa.adapter;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.manrovich.cashflow.domain.kernel.id.UserId;
import ru.manrovich.cashflow.domain.wallet.port.WalletQueryPort;
import ru.manrovich.cashflow.infrastructure.persistence.jpa.repository.WalletJpaRepository;
import ru.manrovich.cashflow.infrastructure.persistence.jpa.repository.projection.WalletListRow;
import ru.manrovich.cashflow.shared.query.Slice;
import ru.manrovich.cashflow.shared.readmodel.WalletListItem;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WalletQueryPortAdapterTest {

    @Mock
    private WalletJpaRepository repository;

    @Test
    void findListItems_shouldMapRowsToSlice_andComputeHasNext_andUseSizePlusOne() {
        WalletQueryPortAdapter adapter = new WalletQueryPortAdapter(repository);

        UUID ownerUuid = UUID.randomUUID();
        UserId ownerId = new UserId(ownerUuid);

        UUID walletId = UUID.randomUUID();

        WalletListRow row = new TestRow(walletId, "Main", "RUB");

        List<WalletListRow> rows = new ArrayList<>();
        for (int i = 0; i < 51; i++) {
            rows.add(row);
        }

        when(repository.findListRows(eq(ownerUuid), any(Pageable.class)))
                .thenReturn(rows);

        WalletQueryPort.WalletSearchCriteria criteria = new WalletQueryPort.WalletSearchCriteria(0, 50);

        Slice<WalletListItem> result = adapter.findListItems(ownerId, criteria);

        assertNotNull(result);
        assertNotNull(result.page());
        assertEquals(0, result.page().number());
        assertEquals(50, result.page().size());
        assertTrue(result.page().hasNext());

        assertNotNull(result.items());
        assertEquals(50, result.items().size());

        WalletListItem item = result.items().get(0);
        assertEquals(walletId, item.id());
        assertEquals("Main", item.name());
        assertEquals("RUB", item.currencyCode());

        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);

        verify(repository).findListRows(eq(ownerUuid), pageableCaptor.capture());

        Pageable used = pageableCaptor.getValue();

        assertEquals(0, used.getPageNumber());
        assertEquals(51, used.getPageSize()); // size + 1

        Sort.Order nameOrder = used.getSort().getOrderFor("name");
        assertNotNull(nameOrder);
        assertEquals(Sort.Direction.ASC, nameOrder.getDirection());

        Sort.Order idOrder = used.getSort().getOrderFor("id");
        assertNotNull(idOrder);
        assertEquals(Sort.Direction.ASC, idOrder.getDirection());
    }

    private static class TestRow implements WalletListRow {

        private final UUID id;
        private final String name;
        private final String currencyCode;

        private TestRow(UUID id, String name, String currencyCode) {
            this.id = id;
            this.name = name;
            this.currencyCode = currencyCode;
        }

        @Override
        public UUID getId() {
            return id;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public String getCurrencyCode() {
            return currencyCode;
        }
    }
}
