package ru.manrovich.cashflow.application.wallet.web.create;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.manrovich.cashflow.application.wallet.usecase.create.CreateWalletCommand;
import ru.manrovich.cashflow.application.wallet.usecase.create.CreateWalletResult;
import ru.manrovich.cashflow.application.wallet.usecase.create.CreateWalletUseCase;

@Component
@RequiredArgsConstructor
public class CreateWalletHandler {
    private final CreateWalletUseCase useCase;

    public CreateWalletResponse handle(CreateWalletRequest request) {
        CreateWalletResult result = useCase.execute(new CreateWalletCommand(
                request.name(),
                request.currencyCode()
        ));

        return new CreateWalletResponse(
                result.id(),
                result.name(),
                result.currencyCode()
        );
    }
}
