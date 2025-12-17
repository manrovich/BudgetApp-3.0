package ru.manrovich.cashflow.infrastructure.ledger.transaction;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import ru.manrovich.cashflow.domain.ledger.transaction.TransactionType;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "ledger_transaction")
public class TransactionEntity {

    @Id
    private UUID id;

    @Column(name = "wallet_id", nullable = false)
    private UUID walletId;

    /**
     * Храним как numeric с большим запасом под крипту/фиат.
     * В PostgreSQL numeric не ограничен, но JPA лучше дать разумные precision/scale.
     */
    @Column(nullable = false, precision = 38, scale = 18)
    private BigDecimal amount;

    @Column(name = "currency_code", nullable = false, length = 10)
    private String currencyCode;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private TransactionType type;
}