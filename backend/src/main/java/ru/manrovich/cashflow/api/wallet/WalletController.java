package ru.manrovich.cashflow.api.wallet;

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
import ru.manrovich.cashflow.api.common.paging.ItemsResponse;
import ru.manrovich.cashflow.api.wallet.dto.CreateWalletRequest;
import ru.manrovich.cashflow.api.wallet.dto.CreateWalletResponse;
import ru.manrovich.cashflow.api.wallet.dto.ListWalletTransactionsRequest;
import ru.manrovich.cashflow.api.wallet.dto.TransactionListItem;
import ru.manrovich.cashflow.application.wallet.command.CreateWalletCommand;
import ru.manrovich.cashflow.application.wallet.command.CreateWalletResult;
import ru.manrovich.cashflow.application.wallet.command.WalletCommandService;
import ru.manrovich.cashflow.application.wallet.query.WalletQueryService;

import java.util.List;

@RestController
@RequestMapping("/api/wallets")
@RequiredArgsConstructor
public class WalletController {

    private final WalletCommandService walletCommandService;
    private final WalletQueryService walletQueryService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CreateWalletResponse create(@Valid @RequestBody CreateWalletRequest request) {
        CreateWalletResult result = walletCommandService.create(new CreateWalletCommand(
                request.name(),
                request.currencyCode()
        ));
        return new CreateWalletResponse(result.id(), result.name(), result.currencyCode());
    }

    @GetMapping("{walletId}/transaction")
    public ItemsResponse<TransactionListItem> list(
            @PathVariable String walletId,
            @Valid @ModelAttribute ListWalletTransactionsRequest request) {
        List<ru.manrovich.cashflow.application.wallet.query.TransactionListItem> items =
                walletQueryService.listWalletTransactions(walletId, request.from(), request.to());
        return ItemsResponse.of(items.stream()
                .map(this::toApiItem)
                .toList());
    }

    private TransactionListItem toApiItem(ru.manrovich.cashflow.application.wallet.query.TransactionListItem item) {
        return new TransactionListItem(
                item.id(),
                item.walletId(),
                item.type(),
                item.amount(),
                item.currencyCode(),
                item.occurredAt(),
                item.categoryId()
        );
    }
}
