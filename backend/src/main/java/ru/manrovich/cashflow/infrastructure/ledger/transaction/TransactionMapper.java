package ru.manrovich.cashflow.infrastructure.ledger.transaction;

import org.springframework.stereotype.Component;
import ru.manrovich.cashflow.domain.ledger.currency.CurrencyCode;
import ru.manrovich.cashflow.domain.ledger.transaction.Money;
import ru.manrovich.cashflow.domain.ledger.transaction.Transaction;
import ru.manrovich.cashflow.domain.ledger.transaction.TransactionId;
import ru.manrovich.cashflow.domain.ledger.wallet.WalletId;
import ru.manrovich.cashflow.infrastructure.mapper.EntityDomainMapper;

@Component
public class TransactionMapper implements EntityDomainMapper<TransactionEntity, Transaction> {

    public Transaction toDomain(TransactionEntity entity) {
        if (entity == null) {
            return null;
        }

        return new Transaction(
                new TransactionId(entity.getId()),
                new WalletId(entity.getWalletId()),
                new Money(entity.getAmount(), new CurrencyCode(entity.getCurrencyCode())),
                entity.getType()
        );
    }

    public TransactionEntity toEntity(Transaction domain) {
        if (domain == null) {
            return null;
        }

        TransactionEntity entity = new TransactionEntity();
        entity.setId(domain.id().value());
        entity.setWalletId(domain.walletId().value());
        entity.setAmount(domain.amount().amount());
        entity.setCurrencyCode(domain.amount().currency().value());
        entity.setType(domain.type());
        return entity;
    }
}
