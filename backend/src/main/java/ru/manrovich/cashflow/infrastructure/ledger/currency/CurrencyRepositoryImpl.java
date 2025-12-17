package ru.manrovich.cashflow.infrastructure.ledger.currency;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.manrovich.cashflow.domain.ledger.currency.CurrencyCode;
import ru.manrovich.cashflow.domain.ledger.currency.CurrencyMeta;
import ru.manrovich.cashflow.domain.ledger.currency.CurrencyRepository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CurrencyRepositoryImpl implements CurrencyRepository {
    private final JpaCurrencyRepository jpaRepository;
    private final CurrencyMapper mapper;

    @Override
    public Optional<CurrencyMeta> findByCode(CurrencyCode code) {
        return jpaRepository.findByCode(code.value())
                .map(mapper::toDomain);
    }
}
