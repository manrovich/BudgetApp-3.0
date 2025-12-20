package ru.manrovich.cashflow.infrastructure.persistence.jpa.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.manrovich.cashflow.domain.kernel.id.TransactionId;
import ru.manrovich.cashflow.domain.kernel.id.UserId;
import ru.manrovich.cashflow.domain.kernel.id.WalletId;
import ru.manrovich.cashflow.domain.transaction.port.TransactionQueryPort;
import ru.manrovich.cashflow.infrastructure.persistence.jpa.repository.TransactionJpaRepository;

import java.math.BigDecimal;

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
}
