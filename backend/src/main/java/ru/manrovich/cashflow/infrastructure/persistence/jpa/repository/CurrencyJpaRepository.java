package ru.manrovich.cashflow.infrastructure.persistence.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.manrovich.cashflow.infrastructure.persistence.jpa.entity.CurrencyEntity;

public interface CurrencyJpaRepository extends JpaRepository<CurrencyEntity, String> {
}
