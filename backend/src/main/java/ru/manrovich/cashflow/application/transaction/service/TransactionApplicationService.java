package ru.manrovich.cashflow.application.transaction.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.manrovich.cashflow.application.common.security.CurrentUserProvider;
import ru.manrovich.cashflow.application.transaction.usecase.TransactionUseCase;
import ru.manrovich.cashflow.application.transaction.usecase.command.CreateTransactionCommand;
import ru.manrovich.cashflow.application.transaction.usecase.result.CreateTransactionResult;
import ru.manrovich.cashflow.domain.kernel.exception.NotFoundException;
import ru.manrovich.cashflow.domain.kernel.id.CategoryId;
import ru.manrovich.cashflow.domain.kernel.id.CurrencyId;
import ru.manrovich.cashflow.domain.kernel.id.TransactionId;
import ru.manrovich.cashflow.domain.kernel.id.UserId;
import ru.manrovich.cashflow.domain.kernel.id.WalletId;
import ru.manrovich.cashflow.domain.kernel.money.Money;
import ru.manrovich.cashflow.domain.reference.category.port.CategoryQueryPort;
import ru.manrovich.cashflow.domain.transaction.model.Transaction;
import ru.manrovich.cashflow.domain.transaction.model.TransactionType;
import ru.manrovich.cashflow.domain.transaction.port.TransactionRepository;
import ru.manrovich.cashflow.domain.wallet.port.WalletQueryPort;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransactionApplicationService implements TransactionUseCase {

    private final TransactionRepository transactionRepository;
    private final WalletQueryPort walletQueryPort;
    private final CategoryQueryPort categoryQueryPort;
    private final CurrentUserProvider currentUserProvider;

    @Override
    @Transactional
    public CreateTransactionResult create(CreateTransactionCommand command) {
        UserId ownerId = currentUserProvider.currentUserId();

        WalletId walletId = new WalletId(UUID.fromString(command.walletId()));

        if (!walletQueryPort.exists(ownerId, walletId)) {
            throw new NotFoundException("Wallet not found: " + walletId.value());
        }

        CurrencyId walletCurrency = walletQueryPort.getCurrencyId(ownerId, walletId);

        TransactionType type = TransactionType.parse(command.type());

        CategoryId categoryId = null;
        if (command.categoryId() != null) {
            categoryId = new CategoryId(UUID.fromString(command.categoryId()));
            if (!categoryQueryPort.exists(ownerId, categoryId)) {
                throw new NotFoundException("Category not found: " + categoryId.value());
            }
        }

        BigDecimal amount = command.amount();
        Instant occurredAt = command.occurredAt();

        Money money = new Money(amount, walletCurrency);

        Transaction transaction = new Transaction(
                new TransactionId(UUID.randomUUID()),
                ownerId,
                walletId,
                categoryId,
                type,
                money,
                occurredAt
        );

        Transaction saved = transactionRepository.save(transaction);

        return new CreateTransactionResult(saved.id().value().toString());
    }
}
