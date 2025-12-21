package ru.manrovich.cashflow.infrastructure.persistence.jpa.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.manrovich.cashflow.domain.kernel.id.CurrencyId;
import ru.manrovich.cashflow.domain.reference.currency.port.CurrencyQueryPort;
import ru.manrovich.cashflow.infrastructure.persistence.jpa.repository.CurrencyJpaRepository;

@Component
@RequiredArgsConstructor
public class CurrencyQueryPortAdapter implements CurrencyQueryPort {

    private final CurrencyJpaRepository jpaRepository;

    @Override
    @Transactional(readOnly = true)
    public boolean exists(CurrencyId code) {
        return jpaRepository.existsById(code.value());
    }
}
