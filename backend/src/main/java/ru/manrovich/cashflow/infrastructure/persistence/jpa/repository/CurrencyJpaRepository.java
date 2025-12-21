package ru.manrovich.cashflow.infrastructure.persistence.jpa.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.manrovich.cashflow.infrastructure.persistence.jpa.entity.CurrencyEntity;
import ru.manrovich.cashflow.infrastructure.persistence.jpa.repository.projection.CurrencyListRow;

import java.util.List;

public interface CurrencyJpaRepository extends JpaRepository<CurrencyEntity, String> {
    @Query("""
           select
             c.code as code,
             c.name as name,
             c.scale as scale,
             c.symbol as symbol
           from CurrencyEntity c
           where (:query is null
              or lower(c.code) like lower(concat('%', :query, '%'))
              or lower(c.name) like lower(concat('%', :query, '%')))
           """)
    List<CurrencyListRow> findListRows(String query, Pageable pageable);
}
