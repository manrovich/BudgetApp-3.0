package ru.manrovich.cashflow.infrastructure.persistence.jpa.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.manrovich.cashflow.domain.kernel.id.TransactionId;
import ru.manrovich.cashflow.domain.kernel.id.UserId;
import ru.manrovich.cashflow.domain.transaction.model.Transaction;
import ru.manrovich.cashflow.domain.transaction.port.TransactionRepository;
import ru.manrovich.cashflow.infrastructure.persistence.jpa.entity.TransactionEntity;
import ru.manrovich.cashflow.infrastructure.persistence.jpa.mapper.TransactionEntityMapper;
import ru.manrovich.cashflow.infrastructure.persistence.jpa.repository.TransactionJpaRepository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class TransactionRepositoryAdapter implements TransactionRepository {

    private final TransactionJpaRepository jpaRepository;
    private final TransactionEntityMapper mapper;

    @Override
    public Transaction save(Transaction transaction) {
        TransactionEntity entity = mapper.toEntity(transaction);
        TransactionEntity saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<Transaction> findById(UserId ownerId, TransactionId id) {
        return jpaRepository.findByIdAndOwnerId(id.value(), ownerId.value())
                .map(mapper::toDomain);
    }

    @Override
    public void deleteById(UserId ownerId, TransactionId id) {
        jpaRepository.deleteByIdAndOwnerId(id.value(), ownerId.value());
    }
}
