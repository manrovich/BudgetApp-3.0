package ru.manrovich.cashflow.application.wallet.web;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.manrovich.cashflow.application.wallet.web.create.CreateWalletHandler;
import ru.manrovich.cashflow.application.wallet.web.create.CreateWalletRequest;
import ru.manrovich.cashflow.application.wallet.web.create.CreateWalletResponse;

@RestController
@RequestMapping("/api/wallets")
@RequiredArgsConstructor
public class WalletController {

    private final CreateWalletHandler createWalletHandler;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CreateWalletResponse create(@RequestBody CreateWalletRequest request) {
        return createWalletHandler.handle(request);
    }
}
