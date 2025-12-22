package ru.manrovich.cashflow.application.wallet.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.manrovich.cashflow.application.wallet.usecase.WalletUseCase;
import ru.manrovich.cashflow.application.wallet.usecase.result.CreateWalletResult;
import ru.manrovich.cashflow.application.wallet.web.dto.CreateWalletRequest;
import ru.manrovich.cashflow.application.wallet.web.dto.CreateWalletResponse;
import ru.manrovich.cashflow.application.wallet.web.dto.ListWalletTransactionsRequest;
import ru.manrovich.cashflow.application.wallet.web.mapper.WalletWebMapper;
import ru.manrovich.cashflow.shared.query.ItemsResponse;
import ru.manrovich.cashflow.shared.readmodel.TransactionListItem;

import java.util.List;

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

    @GetMapping("{walletId}/transaction")
    public ItemsResponse<TransactionListItem> list(
            @PathVariable String walletId,
            @Valid @ModelAttribute ListWalletTransactionsRequest request) {
        List<TransactionListItem> items = useCase.listWalletTransactions(
                mapper.toListWalletTransactionsQuery(walletId, request)
        );
        return ItemsResponse.of(items);
    }
}
