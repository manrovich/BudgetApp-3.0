package ru.manrovich.cashflow.application.wallet.command;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.manrovich.cashflow.application.common.security.CurrentUserProvider;
import ru.manrovich.cashflow.domain.kernel.exception.NotFoundException;
import ru.manrovich.cashflow.domain.kernel.id.CurrencyId;
import ru.manrovich.cashflow.domain.kernel.id.UserId;
import ru.manrovich.cashflow.domain.kernel.id.WalletId;
import ru.manrovich.cashflow.domain.reference.currency.port.CurrencyQueryPort;
import ru.manrovich.cashflow.domain.wallet.model.Wallet;
import ru.manrovich.cashflow.domain.wallet.model.WalletName;
import ru.manrovich.cashflow.domain.wallet.port.WalletRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WalletCommandService {

    private final WalletRepository walletRepository;
    private final CurrencyQueryPort currencyQueryPort;
    private final CurrentUserProvider currentUserProvider;


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
}
