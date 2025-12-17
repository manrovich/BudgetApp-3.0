package ru.manrovich.cashflow.infrastructure.ledger.currency;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import ru.manrovich.cashflow.domain.ledger.currency.CurrencyKind;

import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "currency")
public class CurrencyEntity {

    @Id
    private UUID id;

    @Column(unique = true, nullable = false)
    private String code;

    private String displayName;
    @Enumerated(EnumType.STRING)
    private CurrencyKind kind;
}