package ru.manrovich.cashflow.infrastructure.persistence.jpa.adapter;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import ru.manrovich.cashflow.domain.kernel.id.WalletId;
import ru.manrovich.cashflow.domain.transaction.port.TransactionQueryPort;
import ru.manrovich.cashflow.infrastructure.persistence.jpa.repository.TransactionJpaRepository;
import ru.manrovich.cashflow.infrastructure.persistence.jpa.repository.projection.TransactionListRow;
import ru.manrovich.cashflow.shared.query.Slice;
import ru.manrovich.cashflow.shared.readmodel.TransactionListItem;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
class TransactionQueryPortAdapterTest {

    @Mock
    private TransactionJpaRepository repository;

    @Test
    void findListItems_shouldMapRowsToDomainSlice_andComputeHasNext_andUsePlainStringForAmount() {
        TransactionQueryPortAdapter adapter = new TransactionQueryPortAdapter(repository);

        UUID walletUuid = UUID.randomUUID();
        WalletId walletId = new WalletId(walletUuid);

        UUID txId = UUID.randomUUID();

        // BigDecimal в научной нотации -> toPlainString должен дать "0.0000001"
        BigDecimal amount = new BigDecimal("1E-7");
        Instant occurredAt = Instant.parse("2025-12-01T10:00:00Z");

        TransactionListRow row = new TestRow(
                txId,
                walletUuid,
                "EXPENSE",
                amount,
                "RUB",
                occurredAt,
                null
        );

        // Имитируем size+1 строк, чтобы hasNext стало true
        List<TransactionListRow> rows = new ArrayList<>();
        rows.add(row);
        rows.add(row);
        rows.clear();
        for (int i = 0; i < 51; i++) {
            rows.add(row);
        }

        when(repository.findListRows(eq(walletUuid), isNull(), isNull(), any()))
                .thenReturn(rows);

        TransactionQueryPort.TransactionSearchCriteria criteria =
                new TransactionQueryPort.TransactionSearchCriteria(walletId, null, null, 0, 50);

        Slice<TransactionListItem> result = adapter.findListItems(criteria);

        assertNotNull(result);
        assertNotNull(result.page());
        assertEquals(0, result.page().number());
        assertEquals(50, result.page().size());
        assertTrue(result.page().hasNext());

        assertNotNull(result.items());
        assertEquals(50, result.items().size()); // limit(size)

        TransactionListItem item = result.items().get(0);

        assertEquals(txId, item.id());
        assertEquals(walletUuid, item.walletId());
        assertEquals("EXPENSE", item.type());
        assertEquals("0.0000001", item.amount());
        assertEquals("RUB", item.currencyCode());
        assertEquals(occurredAt, item.occurredAt());
        assertNull(item.categoryId());

        ArgumentCaptor<org.springframework.data.domain.Pageable> pageableCaptor =
                ArgumentCaptor.forClass(org.springframework.data.domain.Pageable.class);

        verify(repository).findListRows(eq(walletUuid), isNull(), isNull(), pageableCaptor.capture());

        org.springframework.data.domain.Pageable usedPageable = pageableCaptor.getValue();

        assertEquals(0, usedPageable.getPageNumber());
        assertEquals(51, usedPageable.getPageSize()); // size+1

        Sort usedSort = usedPageable.getSort();

        Sort.Order occurredOrder = usedSort.getOrderFor("occurredAt");
        assertNotNull(occurredOrder);
        assertEquals(Sort.Direction.DESC, occurredOrder.getDirection());

        Sort.Order idOrder = usedSort.getOrderFor("id");
        assertNotNull(idOrder);
        assertEquals(Sort.Direction.DESC, idOrder.getDirection());
    }

    private static class TestRow implements TransactionListRow {

        private final UUID id;
        private final UUID walletId;
        private final String type;
        private final BigDecimal amount;
        private final String currencyCode;
        private final Instant occurredAt;
        private final UUID categoryId;

        private TestRow(
                UUID id,
                UUID walletId,
                String type,
                BigDecimal amount,
                String currencyCode,
                Instant occurredAt,
                UUID categoryId
        ) {
            this.id = id;
            this.walletId = walletId;
            this.type = type;
            this.amount = amount;
            this.currencyCode = currencyCode;
            this.occurredAt = occurredAt;
            this.categoryId = categoryId;
        }

        @Override
        public UUID getId() {
            return id;
        }

        @Override
        public UUID getWalletId() {
            return walletId;
        }

        @Override
        public String getType() {
            return type;
        }

        @Override
        public BigDecimal getAmount() {
            return amount;
        }

        @Override
        public String getCurrencyCode() {
            return currencyCode;
        }

        @Override
        public Instant getOccurredAt() {
            return occurredAt;
        }

        @Override
        public UUID getCategoryId() {
            return categoryId;
        }
    }
}
