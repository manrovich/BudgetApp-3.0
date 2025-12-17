package ru.manrovich.cashflow.infrastructure.ledger.currency;

import org.springframework.stereotype.Component;
import ru.manrovich.cashflow.domain.ledger.currency.CurrencyCode;
import ru.manrovich.cashflow.domain.ledger.currency.CurrencyMeta;
import ru.manrovich.cashflow.infrastructure.mapper.EntityDomainMapper;

@Component
public class CurrencyMapper implements EntityDomainMapper<CurrencyEntity, CurrencyMeta> {

    @Override
    public CurrencyMeta toDomain(CurrencyEntity entity) {
        return new CurrencyMeta(
                new CurrencyCode(entity.getCode()),
                entity.getDisplayName(),
                entity.getKind(),
                2 // TODO
        );
    }

    @Override
    public CurrencyEntity toEntity(CurrencyMeta meta) {
        CurrencyEntity entity = new CurrencyEntity();
        entity.setCode(meta.code().value());
        entity.setDisplayName(meta.displayName());
        entity.setKind(meta.kind());
        return entity;
    }
}
