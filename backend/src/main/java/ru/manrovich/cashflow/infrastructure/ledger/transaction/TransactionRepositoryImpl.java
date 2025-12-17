package ru.manrovich.cashflow.infrastructure.ledger.transaction;

import org.springframework.stereotype.Repository;
import ru.manrovich.cashflow.domain.ledger.transaction.Transaction;
import ru.manrovich.cashflow.domain.ledger.transaction.TransactionId;
import ru.manrovich.cashflow.domain.ledger.transaction.TransactionRepository;
import ru.manrovich.cashflow.domain.ledger.wallet.WalletId;

import java.util.List;
import java.util.Optional;

@Repository
public class TransactionRepositoryImpl implements TransactionRepository {

    private final JpaTransactionRepository jpaRepository;
    private final TransactionMapper mapper;

    public TransactionRepositoryImpl(JpaTransactionRepository jpaRepository, TransactionMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Optional<Transaction> findById(TransactionId id) {
        return jpaRepository.findById(id.value())
                .map(mapper::toDomain);
    }

    @Override
    public List<Transaction> findByWalletId(WalletId walletId) {
        return jpaRepository.findByWalletId(walletId.value()).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public Transaction save(Transaction transaction) {
        TransactionEntity entity = mapper.toEntity(transaction);
        TransactionEntity saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public void deleteById(TransactionId id) {
        jpaRepository.deleteById(id.value());
    }
}
