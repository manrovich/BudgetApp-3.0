package ru.manrovich.cashflow.domain.wallet.model;

import ru.manrovich.cashflow.domain.kernel.id.CurrencyId;
import ru.manrovich.cashflow.domain.kernel.id.UserId;
import ru.manrovich.cashflow.domain.kernel.id.WalletId;
import ru.manrovich.cashflow.domain.kernel.validation.DomainPreconditions;

public class Wallet {

    private final WalletId id;
    private final UserId ownerId;

    private WalletName name;
    private final CurrencyId currencyId; // неизменяемая валюта

    public Wallet(WalletId id, UserId ownerId, WalletName name, CurrencyId currencyId) {
        this.id = DomainPreconditions.notNull(id, "WalletId must not be null");
        this.ownerId = DomainPreconditions.notNull(ownerId, "OwnerId must not be null");
        this.name = DomainPreconditions.notNull(name, "WalletName must not be null");
        this.currencyId = DomainPreconditions.notNull(currencyId, "CurrencyId must not be null");
    }

    public WalletId id() {
        return id;
    }

    public UserId ownerId() {
        return ownerId;
    }

    public WalletName name() {
        return name;
    }

    public CurrencyId currencyId() {
        return currencyId;
    }

    public void rename(WalletName newName) {
        this.name = DomainPreconditions.notNull(newName, "WalletName must not be null");
    }
}
