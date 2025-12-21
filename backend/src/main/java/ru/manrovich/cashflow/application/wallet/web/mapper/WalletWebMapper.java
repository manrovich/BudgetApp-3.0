package ru.manrovich.cashflow.application.wallet.web.mapper;

import org.springframework.stereotype.Component;
import ru.manrovich.cashflow.application.wallet.usecase.command.CreateWalletCommand;
import ru.manrovich.cashflow.application.wallet.usecase.query.ListWalletTransactionsQuery;
import ru.manrovich.cashflow.application.wallet.usecase.result.CreateWalletResult;
import ru.manrovich.cashflow.application.wallet.web.dto.CreateWalletRequest;
import ru.manrovich.cashflow.application.wallet.web.dto.CreateWalletResponse;
import ru.manrovich.cashflow.application.wallet.web.dto.ListWalletTransactionsRequest;

@Component
public class WalletWebMapper {

    public CreateWalletCommand toCreateCommand(CreateWalletRequest request) {
        return new CreateWalletCommand(request.name(), request.currencyCode());
    }

    public CreateWalletResponse toCreateResponse(CreateWalletResult result) {
        return new CreateWalletResponse(result.id(), result.name(), result.currencyCode());
    }

    public ListWalletTransactionsQuery toListWalletTransactionsQuery(String walletId, ListWalletTransactionsRequest request) {
        return new ListWalletTransactionsQuery(walletId, request.from(), request.to());
    }
}
