package ru.manrovich.cashflow.application.wallet.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.manrovich.cashflow.application.wallet.usecase.WalletUseCase;
import ru.manrovich.cashflow.application.wallet.usecase.result.CreateWalletResult;
import ru.manrovich.cashflow.application.wallet.web.dto.CreateWalletRequest;
import ru.manrovich.cashflow.application.wallet.web.dto.CreateWalletResponse;
import ru.manrovich.cashflow.application.wallet.web.dto.ListWalletsRequest;
import ru.manrovich.cashflow.application.wallet.web.mapper.WalletWebMapper;
import ru.manrovich.cashflow.shared.query.Slice;
import ru.manrovich.cashflow.shared.readmodel.WalletListItem;

@RestController
@RequestMapping("/api/wallets")
@RequiredArgsConstructor
public class WalletController {

    private final WalletUseCase useCase;
    private final WalletWebMapper mapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CreateWalletResponse create(@Valid @RequestBody CreateWalletRequest request) {
        CreateWalletResult result = useCase.create(mapper.toCreateCommand(request));
        return mapper.toCreateResponse(result);
    }

    @GetMapping
    public Slice<WalletListItem> list(@Valid @ModelAttribute ListWalletsRequest request) {
        return useCase.list(mapper.toListQuery(request));
    }
}
