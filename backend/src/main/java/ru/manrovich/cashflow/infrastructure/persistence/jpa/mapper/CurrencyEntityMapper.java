package ru.manrovich.cashflow.infrastructure.persistence.jpa.mapper;

import org.springframework.stereotype.Component;
import ru.manrovich.cashflow.domain.kernel.id.CurrencyId;
import ru.manrovich.cashflow.domain.reference.currency.model.Currency;
import ru.manrovich.cashflow.infrastructure.persistence.jpa.entity.CurrencyEntity;

@Component
public class CurrencyEntityMapper implements EntityDomainMapper<CurrencyEntity, Currency> {

    @Override
    public Currency toDomain(CurrencyEntity entity) {
        return new Currency(
                new CurrencyId(entity.getCode()),
                entity.getName(),
                entity.getScale(),
                entity.getSymbol()
        );
    }

    @Override
    public CurrencyEntity toEntity(Currency domain) {
        return new CurrencyEntity(
                domain.code().value(),
                domain.name(),
                domain.scale(),
                domain.symbol()
        );
    }
}
