package ru.manrovich.cashflow.infrastructure.persistence.jpa.adapter;

import org.springframework.stereotype.Repository;
import ru.manrovich.cashflow.domain.kernel.id.UserId;
import ru.manrovich.cashflow.domain.kernel.id.WalletId;
import ru.manrovich.cashflow.domain.wallet.model.Wallet;
import ru.manrovich.cashflow.domain.wallet.port.WalletRepository;
import ru.manrovich.cashflow.infrastructure.persistence.jpa.mapper.WalletEntityMapper;
import ru.manrovich.cashflow.infrastructure.persistence.jpa.repository.WalletJpaRepository;

import java.util.Optional;

@Repository
public class WalletRepositoryAdapter implements WalletRepository {

    private final WalletJpaRepository walletJpaRepository;
    private final WalletEntityMapper mapper;

    public WalletRepositoryAdapter(WalletJpaRepository walletJpaRepository, WalletEntityMapper mapper) {
        this.walletJpaRepository = walletJpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Wallet save(Wallet wallet) {
        var saved = walletJpaRepository.save(mapper.toEntity(wallet));
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<Wallet> findById(UserId ownerId, WalletId walletId) {
        return walletJpaRepository.findByIdAndOwnerId(walletId.value(), ownerId.value())
                .map(mapper::toDomain);
    }

    @Override
    public void deleteById(UserId ownerId, WalletId walletId) {
        walletJpaRepository.deleteByIdAndOwnerId(walletId.value(), ownerId.value());
    }
}
