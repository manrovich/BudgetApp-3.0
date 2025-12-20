package ru.manrovich.cashflow.infrastructure.persistence.jpa.mapper;

import org.springframework.stereotype.Component;
import ru.manrovich.cashflow.domain.kernel.id.CategoryId;
import ru.manrovich.cashflow.domain.kernel.id.CurrencyId;
import ru.manrovich.cashflow.domain.kernel.id.TransactionId;
import ru.manrovich.cashflow.domain.kernel.id.UserId;
import ru.manrovich.cashflow.domain.kernel.id.WalletId;
import ru.manrovich.cashflow.domain.kernel.money.Money;
import ru.manrovich.cashflow.domain.transaction.model.Transaction;
import ru.manrovich.cashflow.domain.transaction.model.TransactionType;
import ru.manrovich.cashflow.infrastructure.persistence.jpa.entity.TransactionEntity;

import java.util.UUID;

@Component
public class TransactionEntityMapper {

    public TransactionEntity toEntity(Transaction domain) {
        UUID categoryId = domain.categoryId() == null ? null : domain.categoryId().value();

        return new TransactionEntity(
                domain.id().value(),
                domain.ownerId().value(),
                domain.walletId().value(),
                categoryId,
                domain.type().name(),
                domain.money().amount(),
                domain.money().currencyId().value(),
                domain.occurredAt()
        );
    }

    public Transaction toDomain(TransactionEntity entity) {
        CategoryId categoryId = entity.getCategoryId() == null ? null : new CategoryId(entity.getCategoryId());

        Money money = new Money(
                entity.getAmount(),
                new CurrencyId(entity.getCurrencyCode())
        );

        return new Transaction(
                new TransactionId(entity.getId()),
                new UserId(entity.getOwnerId()),
                new WalletId(entity.getWalletId()),
                categoryId,
                TransactionType.valueOf(entity.getType()),
                money,
                entity.getOccurredAt()
        );
    }
}
