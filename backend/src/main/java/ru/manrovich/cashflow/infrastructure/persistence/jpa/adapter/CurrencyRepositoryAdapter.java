package ru.manrovich.cashflow.infrastructure.persistence.jpa.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.manrovich.cashflow.domain.kernel.id.CurrencyId;
import ru.manrovich.cashflow.domain.reference.currency.model.Currency;
import ru.manrovich.cashflow.domain.reference.currency.port.CurrencyRepository;
import ru.manrovich.cashflow.infrastructure.persistence.jpa.entity.CurrencyEntity;
import ru.manrovich.cashflow.infrastructure.persistence.jpa.mapper.CurrencyEntityMapper;
import ru.manrovich.cashflow.infrastructure.persistence.jpa.repository.CurrencyJpaRepository;

import java.util.Collection;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CurrencyRepositoryAdapter implements CurrencyRepository {

    private final CurrencyJpaRepository jpaRepository;
    private final CurrencyEntityMapper mapper;

    @Override
    @Transactional
    public Currency save(Currency currency) {
        CurrencyEntity saved = jpaRepository.save(mapper.toEntity(currency));
        return mapper.toDomain(saved);
    }

    @Override
    @Transactional
    public void saveAll(Collection<Currency> currencies) {
        jpaRepository.saveAll(currencies.stream().map(mapper::toEntity).toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Currency> findByCode(CurrencyId code) {
        return jpaRepository.findById(code.value()).map(mapper::toDomain);
    }
}
