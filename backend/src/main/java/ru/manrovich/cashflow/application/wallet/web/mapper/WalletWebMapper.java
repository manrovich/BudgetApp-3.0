package ru.manrovich.cashflow.application.wallet.web.mapper;

import org.springframework.stereotype.Component;
import ru.manrovich.cashflow.application.wallet.usecase.command.CreateWalletCommand;
import ru.manrovich.cashflow.application.wallet.usecase.result.CreateWalletResult;
import ru.manrovich.cashflow.application.wallet.web.dto.CreateWalletRequest;
import ru.manrovich.cashflow.application.wallet.web.dto.CreateWalletResponse;

@Component
public class WalletWebMapper {

    public CreateWalletCommand toCreateCommand(CreateWalletRequest request) {
        return new CreateWalletCommand(request.name(), request.currencyCode());
    }

    public CreateWalletResponse toCreateResponse(CreateWalletResult result) {
        return new CreateWalletResponse(result.id(), result.name(), result.currencyCode());
    }
}
