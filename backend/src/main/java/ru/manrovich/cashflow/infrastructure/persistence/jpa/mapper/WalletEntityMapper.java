package ru.manrovich.cashflow.infrastructure.persistence.jpa.mapper;

import org.springframework.stereotype.Component;
import ru.manrovich.cashflow.domain.kernel.id.CurrencyId;
import ru.manrovich.cashflow.domain.kernel.id.UserId;
import ru.manrovich.cashflow.domain.kernel.id.WalletId;
import ru.manrovich.cashflow.domain.wallet.model.Wallet;
import ru.manrovich.cashflow.domain.wallet.model.WalletName;
import ru.manrovich.cashflow.infrastructure.persistence.jpa.entity.WalletEntity;

@Component
public class WalletEntityMapper {

    public WalletEntity toEntity(Wallet wallet) {
        return new WalletEntity(
                wallet.id().value(),
                wallet.ownerId().value(),
                wallet.name().value(),
                wallet.currencyId().value()
        );
    }

    public Wallet toDomain(WalletEntity entity) {
        return new Wallet(
                new WalletId(entity.getId()),
                new UserId(entity.getOwnerId()),
                new WalletName(entity.getName()),
                new CurrencyId(entity.getCurrencyCode())
        );
    }
}
