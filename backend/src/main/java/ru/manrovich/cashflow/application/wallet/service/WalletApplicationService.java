package ru.manrovich.cashflow.application.wallet.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.manrovich.cashflow.application.common.security.CurrentUserProvider;
import ru.manrovich.cashflow.application.wallet.usecase.WalletUseCase;
import ru.manrovich.cashflow.application.wallet.usecase.command.CreateWalletCommand;
import ru.manrovich.cashflow.application.wallet.usecase.query.ListWalletsQuery;
import ru.manrovich.cashflow.application.wallet.usecase.result.CreateWalletResult;
import ru.manrovich.cashflow.domain.kernel.exception.NotFoundException;
import ru.manrovich.cashflow.domain.kernel.id.CurrencyId;
import ru.manrovich.cashflow.domain.kernel.id.UserId;
import ru.manrovich.cashflow.domain.kernel.id.WalletId;
import ru.manrovich.cashflow.domain.reference.currency.port.CurrencyQueryPort;
import ru.manrovich.cashflow.domain.wallet.model.Wallet;
import ru.manrovich.cashflow.domain.wallet.model.WalletName;
import ru.manrovich.cashflow.domain.wallet.port.WalletQueryPort;
import ru.manrovich.cashflow.domain.wallet.port.WalletRepository;
import ru.manrovich.cashflow.shared.query.Slice;
import ru.manrovich.cashflow.shared.readmodel.WalletListItem;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WalletApplicationService implements WalletUseCase {

    private final WalletRepository walletRepository;
    private final CurrencyQueryPort currencyQueryPort;
    private final WalletQueryPort walletQueryPort;
    private final CurrentUserProvider currentUserProvider;

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
    public Slice<WalletListItem> list(ListWalletsQuery query) {
        UserId ownerId = currentUserProvider.currentUserId();

        int page = query.page() == null ? 0 : Math.max(query.page(), 0);
        int size = query.size() == null ? 50 : Math.min(Math.max(query.size(), 1), 200);

        WalletQueryPort.WalletSearchCriteria criteria = new WalletQueryPort.WalletSearchCriteria(page, size);

        return walletQueryPort.findListItems(ownerId, criteria);
    }
}
