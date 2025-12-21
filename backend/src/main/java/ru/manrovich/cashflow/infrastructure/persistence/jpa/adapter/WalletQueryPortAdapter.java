package ru.manrovich.cashflow.infrastructure.persistence.jpa.adapter;

import org.springframework.stereotype.Repository;
import ru.manrovich.cashflow.domain.kernel.exception.NotFoundException;
import ru.manrovich.cashflow.domain.kernel.id.CurrencyId;
import ru.manrovich.cashflow.domain.kernel.id.UserId;
import ru.manrovich.cashflow.domain.kernel.id.WalletId;
import ru.manrovich.cashflow.domain.wallet.port.WalletQueryPort;
import ru.manrovich.cashflow.infrastructure.persistence.jpa.repository.WalletJpaRepository;

@Repository
public class WalletQueryPortAdapter implements WalletQueryPort {

    private final WalletJpaRepository walletJpaRepository;

    public WalletQueryPortAdapter(WalletJpaRepository walletJpaRepository) {
        this.walletJpaRepository = walletJpaRepository;
    }

    @Override
    public boolean exists(UserId ownerId, WalletId walletId) {
        return walletJpaRepository.existsByIdAndOwnerId(walletId.value(), ownerId.value());
    }

    @Override
    public CurrencyId getCurrencyId(UserId ownerId, WalletId walletId) {
        String code = walletJpaRepository.findCurrencyCodeByIdAndOwnerId(ownerId.value(), walletId.value())
                .orElseThrow(() -> new NotFoundException("Wallet not found: " + walletId.value()));
        return new CurrencyId(code);
    }
}
