package ru.manrovich.cashflow.infrastructure.ledger.wallet;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "wallet")
public class WalletEntity {

    @Id
    private UUID id;

    @Column(name = "name")
    private String name;

    /**
     * Храним как numeric с большим запасом под крипту/фиат.
     * В PostgreSQL numeric не ограничен, но JPA лучше дать разумные precision/scale.
     */
    @Column(nullable = false, precision = 38, scale = 18)
    private BigDecimal amount;

    @Column(name = "currency_code", nullable = false, length = 10)
    private String currencyCode;
}
