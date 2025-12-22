package ru.manrovich.cashflow.infrastructure.persistence.jpa.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.manrovich.cashflow.domain.kernel.id.TransactionId;
import ru.manrovich.cashflow.domain.kernel.id.UserId;
import ru.manrovich.cashflow.domain.kernel.id.WalletId;
import ru.manrovich.cashflow.domain.transaction.port.TransactionQueryPort;
import ru.manrovich.cashflow.domain.transaction.port.filter.TransactionFilter;
import ru.manrovich.cashflow.infrastructure.persistence.jpa.repository.TransactionJpaRepository;
import ru.manrovich.cashflow.shared.readmodel.TransactionListItem;

import java.math.BigDecimal;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class TransactionQueryPortAdapter implements TransactionQueryPort {

    private final TransactionJpaRepository repository;

    @Override
    public boolean exists(UserId ownerId, TransactionId id) {
        return repository.existsByOwnerIdAndId(ownerId.value(), id.value());
    }

    @Override
    public boolean existsByWalletId(UserId ownerId, WalletId walletId) {
        return repository.existsByOwnerIdAndWalletId(ownerId.value(), walletId.value());
    }

    @Override
    public BigDecimal sumAmountsByWalletId(UserId ownerId, WalletId walletId) {
        return repository.sumAmountsByOwnerIdAndWalletId(ownerId.value(), walletId.value());
    }

    @Override
    public List<TransactionListItem> findListItems(TransactionFilter filter) {
        return repository.findListRows(
                filter.ownerId().value(),
                filter.walletId().value(),
                filter.from(),
                filter.to()).stream()
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
    }
}
