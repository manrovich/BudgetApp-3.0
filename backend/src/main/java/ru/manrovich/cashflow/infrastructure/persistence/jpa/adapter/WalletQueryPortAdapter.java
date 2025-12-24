package ru.manrovich.cashflow.infrastructure.persistence.jpa.adapter;

import org.springframework.stereotype.Repository;
import ru.manrovich.cashflow.domain.kernel.id.CurrencyId;
import ru.manrovich.cashflow.domain.kernel.id.UserId;
import ru.manrovich.cashflow.domain.kernel.id.WalletId;
import ru.manrovich.cashflow.domain.wallet.port.WalletQueryPort;
import ru.manrovich.cashflow.domain.wallet.port.WalletSnapshot;
import ru.manrovich.cashflow.infrastructure.persistence.jpa.repository.WalletJpaRepository;

import java.util.Optional;

@Repository
public class WalletQueryPortAdapter implements WalletQueryPort {

    private final WalletJpaRepository walletJpaRepository;

    public WalletQueryPortAdapter(WalletJpaRepository walletJpaRepository) {
        this.walletJpaRepository = walletJpaRepository;
    }

    @Override
    public Optional<WalletSnapshot> findSnapshot(UserId ownerId, WalletId walletId) {
        return walletJpaRepository.findCurrencyCodeByIdAndOwnerId(ownerId.value(), walletId.value())
                .map(CurrencyId::new)
                .map(WalletSnapshot::new);
    }
}
