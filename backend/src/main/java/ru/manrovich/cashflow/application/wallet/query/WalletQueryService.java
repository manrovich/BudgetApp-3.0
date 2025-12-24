package ru.manrovich.cashflow.application.wallet.query;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.manrovich.cashflow.application.common.security.CurrentUserProvider;
import ru.manrovich.cashflow.application.transaction.query.TransactionReadRepository;
import ru.manrovich.cashflow.domain.kernel.exception.ValidationException;
import ru.manrovich.cashflow.domain.kernel.exception.NotFoundException;
import ru.manrovich.cashflow.domain.kernel.id.UserId;
import ru.manrovich.cashflow.domain.kernel.id.WalletId;
import ru.manrovich.cashflow.domain.wallet.port.WalletQueryPort;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WalletQueryService {

    private final WalletQueryPort walletQueryPort;
    private final TransactionReadRepository transactionReadRepository;
    private final CurrentUserProvider currentUserProvider;

    public List<TransactionListItem> listWalletTransactions(String walletId, Instant from, Instant to) {
        UserId ownerId = currentUserProvider.currentUserId();

        if (from.isAfter(to)) {
            throw new ValidationException("'from' must be <= 'to'");
        }

        if (walletId == null) {
            throw new ValidationException("WalletId must not be null");
        }

        WalletId parsedWalletId = new WalletId(UUID.fromString(walletId));

        if (walletQueryPort.findSnapshot(ownerId, parsedWalletId).isEmpty()) {
            throw new NotFoundException("Wallet not found: " + parsedWalletId.value());
        }

        return transactionReadRepository.findWalletTransactions(
                ownerId,
                parsedWalletId,
                from,
                to
        );
    }
}
