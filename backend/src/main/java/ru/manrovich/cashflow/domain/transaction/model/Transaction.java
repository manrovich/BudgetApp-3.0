package ru.manrovich.cashflow.domain.transaction.model;

import ru.manrovich.cashflow.domain.kernel.id.CategoryId;
import ru.manrovich.cashflow.domain.kernel.id.TransactionId;
import ru.manrovich.cashflow.domain.kernel.id.UserId;
import ru.manrovich.cashflow.domain.kernel.id.WalletId;
import ru.manrovich.cashflow.domain.kernel.money.Money;
import ru.manrovich.cashflow.domain.kernel.validation.DomainPreconditions;

import java.time.Instant;

public class Transaction {

    private final TransactionId id;
    private final UserId ownerId;
    private final WalletId walletId;
    private final CategoryId categoryId;
    private final Money money;
    private final Instant occurredAt;

    public Transaction(TransactionId id,
                       UserId ownerId,
                       WalletId walletId,
                       CategoryId categoryId,
                       Money money,
                       Instant occurredAt) {
        this.id = DomainPreconditions.notNull(id, "TransactionId must not be null");
        this.ownerId = DomainPreconditions.notNull(ownerId, "OwnerId must not be null");
        this.walletId = DomainPreconditions.notNull(walletId, "WalletId must not be null");
        this.money = DomainPreconditions.notNull(money, "Money must not be null");
        this.occurredAt = DomainPreconditions.notNull(occurredAt, "OccurredAt must not be null");
        this.categoryId = categoryId;
    }

    public TransactionId id() {
        return id;
    }

    public UserId ownerId() {
        return ownerId;
    }

    public WalletId walletId() {
        return walletId;
    }

    public CategoryId categoryId() {
        return categoryId;
    }

    public Money money() {
        return money;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}
