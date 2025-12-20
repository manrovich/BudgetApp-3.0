package ru.manrovich.cashflow.domain.transaction.model;

import ru.manrovich.cashflow.domain.kernel.id.CategoryId;
import ru.manrovich.cashflow.domain.kernel.id.TransactionId;
import ru.manrovich.cashflow.domain.kernel.id.UserId;
import ru.manrovich.cashflow.domain.kernel.id.WalletId;
import ru.manrovich.cashflow.domain.kernel.money.Money;
import ru.manrovich.cashflow.domain.kernel.validation.DomainPreconditions;

import java.math.BigDecimal;
import java.time.Instant;

public class Transaction {

    private final TransactionId id;
    private final UserId ownerId;
    private final WalletId walletId;
    private final CategoryId categoryId;
    private final TransactionType type;
    private final Money money;
    private final Instant occurredAt;

    public Transaction(TransactionId id,
                       UserId ownerId,
                       WalletId walletId,
                       CategoryId categoryId,
                       TransactionType type,
                       Money money,
                       Instant occurredAt) {
        this.id = DomainPreconditions.notNull(id, "TransactionId must not be null");
        this.ownerId = DomainPreconditions.notNull(ownerId, "OwnerId must not be null");
        this.walletId = DomainPreconditions.notNull(walletId, "WalletId must not be null");
        this.type = DomainPreconditions.notNull(type, "TransactionType must not be null");
        this.money = DomainPreconditions.notNull(money, "Money must not be null");
        this.occurredAt = DomainPreconditions.notNull(occurredAt, "OccurredAt must not be null");
        this.categoryId = categoryId;

        DomainPreconditions.check(money.amount().compareTo(BigDecimal.ZERO) > 0,
                "Transaction amount must be > 0 (sign is defined by TransactionType)");
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

    public TransactionType type() {
        return type;
    }

    public Money money() {
        return money;
    }

    public Instant occurredAt() {
        return occurredAt;
    }

    /**
     * Эффективное значение для расчетов
     */
    public Money effect() {
        return switch (type) {
            case INCOME -> money;
            case EXPENSE -> money.negate();
        };
    }
}
