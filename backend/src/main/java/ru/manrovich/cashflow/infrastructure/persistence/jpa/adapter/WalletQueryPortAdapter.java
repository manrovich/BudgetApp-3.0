package ru.manrovich.cashflow.infrastructure.persistence.jpa.adapter;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import ru.manrovich.cashflow.domain.kernel.exception.NotFoundException;
import ru.manrovich.cashflow.domain.kernel.id.CurrencyId;
import ru.manrovich.cashflow.domain.kernel.id.UserId;
import ru.manrovich.cashflow.domain.kernel.id.WalletId;
import ru.manrovich.cashflow.domain.wallet.port.WalletQueryPort;
import ru.manrovich.cashflow.infrastructure.persistence.jpa.repository.WalletJpaRepository;
import ru.manrovich.cashflow.infrastructure.persistence.jpa.repository.projection.WalletListRow;
import ru.manrovich.cashflow.shared.query.Slice;
import ru.manrovich.cashflow.shared.query.SlicePage;
import ru.manrovich.cashflow.shared.readmodel.WalletListItem;

import java.util.List;

@Repository
public class WalletQueryPortAdapter implements WalletQueryPort {

    private final WalletJpaRepository repository;

    public WalletQueryPortAdapter(WalletJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public boolean exists(UserId ownerId, WalletId walletId) {
        return repository.existsByIdAndOwnerId(walletId.value(), ownerId.value());
    }

    @Override
    public CurrencyId getCurrencyId(UserId ownerId, WalletId walletId) {
        String code = repository.findCurrencyCodeByIdAndOwnerId(ownerId.value(), walletId.value())
                .orElseThrow(() -> new NotFoundException("Wallet not found: " + walletId.value()));
        return new CurrencyId(code);
    }

    @Override
    public Slice<WalletListItem> findListItems(UserId ownerId, WalletSearchCriteria criteria) {
        int size = Math.min(Math.max(criteria.size(), 1), 200);
        int page = Math.max(criteria.page(), 0);

        PageRequest pageable = PageRequest.of(
                page,
                size + 1,
                Sort.by(Sort.Direction.ASC, "name")
                        .and(Sort.by(Sort.Direction.ASC, "id"))
        );

        List<WalletListRow> rows = repository.findListRows(ownerId.value(), pageable);

        boolean hasNext = rows.size() > size;

        List<WalletListItem> items = rows.stream()
                .limit(size)
                .map(row -> new WalletListItem(
                        row.getId(),
                        row.getName(),
                        row.getCurrencyCode()
                ))
                .toList();

        return new Slice<>(items, new SlicePage(page, size, hasNext));
    }
}
