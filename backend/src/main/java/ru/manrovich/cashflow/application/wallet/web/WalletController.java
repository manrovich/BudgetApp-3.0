package ru.manrovich.cashflow.application.wallet.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.manrovich.cashflow.application.wallet.usecase.WalletUseCase;
import ru.manrovich.cashflow.application.wallet.usecase.result.CreateWalletResult;
import ru.manrovich.cashflow.application.wallet.web.dto.CreateWalletRequest;
import ru.manrovich.cashflow.application.wallet.web.dto.CreateWalletResponse;
import ru.manrovich.cashflow.application.wallet.web.mapper.WalletWebMapper;

@RestController
@RequestMapping("/api/wallets")
@RequiredArgsConstructor
public class WalletController {

    private final WalletUseCase walletUseCase;
    private final WalletWebMapper webMapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CreateWalletResponse create(@Valid @RequestBody CreateWalletRequest request) {
        CreateWalletResult result = walletUseCase.create(webMapper.toCreateCommand(request));
        return webMapper.toCreateResponse(result);
    }
}
