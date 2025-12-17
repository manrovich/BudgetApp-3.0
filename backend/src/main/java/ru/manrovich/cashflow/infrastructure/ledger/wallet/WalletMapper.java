package ru.manrovich.cashflow.infrastructure.ledger.wallet;

import org.springframework.stereotype.Component;
import ru.manrovich.cashflow.domain.ledger.currency.CurrencyCode;
import ru.manrovich.cashflow.domain.ledger.transaction.Money;
import ru.manrovich.cashflow.domain.ledger.wallet.Wallet;
import ru.manrovich.cashflow.domain.ledger.wallet.WalletId;
import ru.manrovich.cashflow.infrastructure.mapper.EntityDomainMapper;

@Component
public class WalletMapper implements EntityDomainMapper<WalletEntity, Wallet> {
    @Override
    public Wallet toDomain(WalletEntity entity) {
        if (entity == null) {
            return null;
        }

        return new Wallet(
                new WalletId(entity.getId()),
                new Money(entity.getAmount(), new CurrencyCode(entity.getCurrencyCode())),
                entity.getName()
        );
    }

    @Override
    public WalletEntity toEntity(Wallet domain) {
        if (domain == null) {
            return null;
        }

        WalletEntity entity = new WalletEntity();
        entity.setId(domain.id().value());
        entity.setCurrencyCode(domain.amount().currency().value());
        entity.setAmount(domain.amount().amount());
        entity.setName(domain.name());
        return entity;
    }
}
