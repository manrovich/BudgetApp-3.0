package ru.manrovich.cashflow.domain.wallet.model;

import ru.manrovich.cashflow.domain.kernel.validation.DomainPreconditions;

public record WalletName(String value) {

    public static final int MAX_LENGTH = 64;

    public WalletName {
        DomainPreconditions.notBlank(value, "Wallet name must not be blank");
        DomainPreconditions.check(value.length() <= MAX_LENGTH,
                "Wallet name length must be <= " + MAX_LENGTH);
    }
}
