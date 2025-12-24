package ru.manrovich.cashflow.infrastructure.query.transaction;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.manrovich.cashflow.application.transaction.query.TransactionReadRepository;
import ru.manrovich.cashflow.application.wallet.query.TransactionListItem;
import ru.manrovich.cashflow.domain.kernel.id.UserId;
import ru.manrovich.cashflow.domain.kernel.id.WalletId;
import ru.manrovich.cashflow.infrastructure.persistence.jpa.repository.TransactionJpaRepository;

import java.time.Instant;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class TransactionReadRepositoryAdapter implements TransactionReadRepository {

    private final TransactionJpaRepository transactionJpaRepository;

    @Override
    public List<TransactionListItem> findWalletTransactions(UserId ownerId, WalletId walletId, Instant from, Instant to) {
        return transactionJpaRepository.findListRows(ownerId.value(), walletId.value(), from, to).stream()
                .map(this::toListItem)
                .toList();
    }

    private TransactionListItem toListItem(TransactionListRow row) {
        return new TransactionListItem(
                row.getId().toString(),
                row.getWalletId().toString(),
                row.getType(),
                row.getAmount().toPlainString(),
                row.getCurrencyCode(),
                row.getOccurredAt(),
                row.getCategoryId() == null ? null : row.getCategoryId().toString()
        );
    }
}
