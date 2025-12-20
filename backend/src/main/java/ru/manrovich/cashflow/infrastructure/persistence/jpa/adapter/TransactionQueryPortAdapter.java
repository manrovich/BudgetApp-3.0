package ru.manrovich.cashflow.infrastructure.persistence.jpa.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import ru.manrovich.cashflow.domain.kernel.id.TransactionId;
import ru.manrovich.cashflow.domain.kernel.id.UserId;
import ru.manrovich.cashflow.domain.kernel.id.WalletId;
import ru.manrovich.cashflow.domain.transaction.port.TransactionQueryPort;
import ru.manrovich.cashflow.infrastructure.persistence.jpa.repository.TransactionJpaRepository;
import ru.manrovich.cashflow.infrastructure.persistence.jpa.repository.projection.TransactionListRow;
import ru.manrovich.cashflow.shared.query.Slice;
import ru.manrovich.cashflow.shared.query.SlicePage;
import ru.manrovich.cashflow.shared.readmodel.TransactionListItem;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class TransactionQueryPortAdapter implements TransactionQueryPort {

    private final TransactionJpaRepository jpaRepository;

    @Override
    public boolean exists(UserId ownerId, TransactionId id) {
        return jpaRepository.existsByOwnerIdAndId(ownerId.value(), id.value());
    }

    @Override
    public boolean existsByWalletId(UserId ownerId, WalletId walletId) {
        return jpaRepository.existsByOwnerIdAndWalletId(ownerId.value(), walletId.value());
    }

    @Override
    public BigDecimal sumAmountsByWalletId(UserId ownerId, WalletId walletId) {
        return jpaRepository.sumAmountsByOwnerIdAndWalletId(ownerId.value(), walletId.value());
    }

    @Override
    public Slice<TransactionListItem> findListItems(TransactionSearchCriteria criteria) {
        int size = Math.min(Math.max(criteria.size(), 1), 200);
        int page = Math.max(criteria.page(), 0);

        PageRequest pageable = PageRequest.of(
                page,
                size + 1,
                Sort.by(Sort.Direction.DESC, "occurredAt")
                        .and(Sort.by(Sort.Direction.DESC, "id"))
        );

        UUID walletId = criteria.walletId() == null ? null : criteria.walletId().value();

        List<TransactionListRow> rows = jpaRepository.findListRows(walletId, criteria.from(), criteria.to(), pageable);

        boolean hasNext = rows.size() > size;

        List<TransactionListItem> items = rows.stream()
                .limit(size)
                .map(row -> new TransactionListItem(
                        row.getId(),
                        row.getWalletId(),
                        row.getType(),
                        row.getAmount().toPlainString(),
                        row.getCurrencyCode(),
                        row.getOccurredAt(),
                        row.getCategoryId()
                ))
                .toList();

        return new Slice<>(items, new SlicePage(page, size, hasNext));
    }
}
