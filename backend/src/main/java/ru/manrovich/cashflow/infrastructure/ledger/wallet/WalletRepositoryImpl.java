package ru.manrovich.cashflow.infrastructure.ledger.wallet;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.manrovich.cashflow.domain.ledger.wallet.Wallet;
import ru.manrovich.cashflow.domain.ledger.wallet.WalletId;
import ru.manrovich.cashflow.domain.ledger.wallet.WalletRepository;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class WalletRepositoryImpl implements WalletRepository {

    private final JpaWalletRepository jpaRepository;
    private final WalletMapper mapper;

    @Override
    public Optional<Wallet> findById(WalletId id) {
        return jpaRepository.findById(id.value())
                .map(mapper::toDomain);
    }
}
