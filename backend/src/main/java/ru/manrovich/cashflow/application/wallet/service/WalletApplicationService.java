package ru.manrovich.cashflow.application.wallet.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.manrovich.cashflow.application.common.security.CurrentUserProvider;
import ru.manrovich.cashflow.application.wallet.usecase.WalletUseCase;
import ru.manrovich.cashflow.application.wallet.usecase.command.CreateWalletCommand;
import ru.manrovich.cashflow.application.wallet.usecase.query.ListWalletTransactionsQuery;
import ru.manrovich.cashflow.application.wallet.usecase.result.CreateWalletResult;
import ru.manrovich.cashflow.domain.kernel.exception.NotFoundException;
import ru.manrovich.cashflow.domain.kernel.exception.ValidationException;
import ru.manrovich.cashflow.domain.kernel.id.CurrencyId;
import ru.manrovich.cashflow.domain.kernel.id.UserId;
import ru.manrovich.cashflow.domain.kernel.id.WalletId;
import ru.manrovich.cashflow.domain.reference.currency.port.CurrencyQueryPort;
import ru.manrovich.cashflow.domain.transaction.port.TransactionQueryPort;
import ru.manrovich.cashflow.domain.transaction.port.filter.TransactionFilter;
import ru.manrovich.cashflow.domain.wallet.model.Wallet;
import ru.manrovich.cashflow.domain.wallet.model.WalletName;
import ru.manrovich.cashflow.domain.wallet.port.WalletQueryPort;
import ru.manrovich.cashflow.domain.wallet.port.WalletRepository;
import ru.manrovich.cashflow.shared.readmodel.TransactionListItem;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WalletApplicationService implements WalletUseCase {

    private final WalletRepository walletRepository;
    private final CurrencyQueryPort currencyQueryPort;
    private final WalletQueryPort walletQueryPort;
    private final CurrentUserProvider currentUserProvider;

    private final TransactionQueryPort transactionQueryPort;


    @Override
    @Transactional
    public CreateWalletResult create(CreateWalletCommand command) {
        UserId ownerId = currentUserProvider.currentUserId();

        CurrencyId currencyId = new CurrencyId(command.currencyCode());
        if (!currencyQueryPort.exists(currencyId)) {
            throw new NotFoundException("Currency not found: " + currencyId.value());
        }

        Wallet wallet = new Wallet(
                new WalletId(UUID.randomUUID()),
                ownerId,
                new WalletName(command.name()),
                currencyId
        );

        Wallet saved = walletRepository.save(wallet);

        return new CreateWalletResult(
                saved.id().value().toString(),
                saved.name().value(),
                saved.currencyId().value()
        );
    }

    @Override
    public List<TransactionListItem> listWalletTransactions(ListWalletTransactionsQuery query) {
        UserId ownerId = currentUserProvider.currentUserId();

        if (query.from().isAfter(query.to())) {
            throw new ValidationException("'from' must be <= 'to'");
        }

        WalletId walletId = query.walletId() == null ? null
                : new WalletId(UUID.fromString(query.walletId()));

        if (walletId == null) {
            throw new ValidationException("WalletId must not be null");
        }

        if (!walletQueryPort.exists(ownerId, walletId)) {
            throw new ValidationException("Wallet not found");
        }

        return transactionQueryPort.findListItems(new TransactionFilter(
                ownerId,
                walletId,
                query.from(),
                query.to()
        ));
    }
}
