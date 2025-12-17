package ru.manrovich.cashflow.domain.ledger.transaction;

import ru.manrovich.cashflow.domain.ledger.wallet.WalletId;

import java.time.Instant;

public record Transaction(
        TransactionId id,
        WalletId walletId,
        Money amount,
        TransactionType type
) {

    public Transaction {
        if (amount == null || amount.amount().signum() == 0) {
            throw new IllegalArgumentException("Amount must be non-zero");
        }
        if (walletId == null) {
            throw new IllegalArgumentException("Wallet must be specified");
        }
    }

//    public Transaction changeAmount(Money newAmount, Instant modifiedAt) {
//        return new Transaction(
//                this.id,
//                this.walletId,
//                newAmount,
//                this.type
//        );
//    }
}