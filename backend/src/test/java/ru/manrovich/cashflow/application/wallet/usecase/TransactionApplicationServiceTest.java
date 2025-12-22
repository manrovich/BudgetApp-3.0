package ru.manrovich.cashflow.application.wallet.usecase;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.manrovich.cashflow.application.common.security.CurrentUserProvider;
import ru.manrovich.cashflow.application.transaction.service.TransactionApplicationService;
import ru.manrovich.cashflow.application.transaction.usecase.command.CreateTransactionCommand;
import ru.manrovich.cashflow.application.transaction.usecase.result.CreateTransactionResult;
import ru.manrovich.cashflow.domain.kernel.exception.NotFoundException;
import ru.manrovich.cashflow.domain.kernel.exception.ValidationException;
import ru.manrovich.cashflow.domain.kernel.id.CategoryId;
import ru.manrovich.cashflow.domain.kernel.id.CurrencyId;
import ru.manrovich.cashflow.domain.kernel.id.WalletId;
import ru.manrovich.cashflow.domain.reference.category.port.CategoryQueryPort;
import ru.manrovich.cashflow.domain.transaction.model.Transaction;
import ru.manrovich.cashflow.domain.transaction.model.TransactionType;
import ru.manrovich.cashflow.domain.transaction.port.TransactionQueryPort;
import ru.manrovich.cashflow.domain.transaction.port.TransactionRepository;
import ru.manrovich.cashflow.domain.wallet.port.WalletQueryPort;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static ru.manrovich.cashflow.testing.data.TestUsers.USER_1;

@ExtendWith(MockitoExtension.class)
class TransactionApplicationServiceTest {

    @Mock
    TransactionRepository transactionRepository;
    @Mock
    WalletQueryPort walletQueryPort;
    @Mock
    CategoryQueryPort categoryQueryPort;
    @Mock
    TransactionQueryPort transactionQueryPort;
    @Mock
    CurrentUserProvider currentUserProvider;

    @InjectMocks
    TransactionApplicationService service;

    @Test
    void create_shouldCreateTransaction_whenOk() {
        when(currentUserProvider.currentUserId()).thenReturn(USER_1);

        UUID walletUuid = UUID.randomUUID();
        WalletId walletId = new WalletId(walletUuid);

        when(walletQueryPort.exists(USER_1, walletId)).thenReturn(true);
        when(walletQueryPort.getCurrencyId(USER_1, walletId)).thenReturn(new CurrencyId("RUB"));

        UUID categoryUuid = UUID.randomUUID();
        CategoryId categoryId = new CategoryId(categoryUuid);
        when(categoryQueryPort.exists(USER_1, categoryId)).thenReturn(true);

        when(transactionRepository.save(any(Transaction.class)))
                .thenAnswer(inv -> inv.getArgument(0, Transaction.class));

        CreateTransactionCommand cmd = new CreateTransactionCommand(
                walletUuid.toString(),
                categoryUuid.toString(),
                "EXPENSE",
                new BigDecimal("100.00"),
                Instant.parse("2025-01-01T10:00:00Z")
        );

        CreateTransactionResult result = service.create(cmd);

        assertNotNull(result.id());

        ArgumentCaptor<Transaction> captor = ArgumentCaptor.forClass(Transaction.class);
        verify(transactionRepository).save(captor.capture());

        Transaction saved = captor.getValue();
        assertEquals(USER_1.value(), saved.ownerId().value());
        assertEquals(walletUuid, saved.walletId().value());
        assertEquals(categoryUuid, saved.categoryId().value());
        assertEquals(TransactionType.EXPENSE, saved.type());
        assertEquals("RUB", saved.money().currencyId().value());
        assertEquals(0, saved.money().amount().compareTo(new BigDecimal("100.00")));
        assertEquals("2025-01-01T10:00:00Z", saved.occurredAt().toString());
    }

    @Test
    void create_shouldThrowNotFound_whenWalletMissing() {
        when(currentUserProvider.currentUserId()).thenReturn(USER_1);

        UUID walletUuid = UUID.randomUUID();
        WalletId walletId = new WalletId(walletUuid);
        when(walletQueryPort.exists(USER_1, walletId)).thenReturn(false);

        CreateTransactionCommand cmd = new CreateTransactionCommand(
                walletUuid.toString(),
                null,
                "INCOME",
                new BigDecimal("10"),
                Instant.parse("2025-01-01T10:00:00Z")
        );

        assertThrows(NotFoundException.class, () -> service.create(cmd));
        verify(transactionRepository, never()).save(any());
    }

    @Test
    void create_shouldThrowNotFound_whenCategoryMissing() {
        when(currentUserProvider.currentUserId()).thenReturn(USER_1);

        UUID walletUuid = UUID.randomUUID();
        WalletId walletId = new WalletId(walletUuid);
        when(walletQueryPort.exists(USER_1, walletId)).thenReturn(true);
        when(walletQueryPort.getCurrencyId(USER_1, walletId)).thenReturn(new CurrencyId("RUB"));

        UUID categoryUuid = UUID.randomUUID();
        CategoryId categoryId = new CategoryId(categoryUuid);
        when(categoryQueryPort.exists(USER_1, categoryId)).thenReturn(false);

        CreateTransactionCommand cmd = new CreateTransactionCommand(
                walletUuid.toString(),
                categoryUuid.toString(),
                "INCOME",
                new BigDecimal("10"),
                Instant.parse("2025-01-01T10:00:00Z")
        );

        assertThrows(NotFoundException.class, () -> service.create(cmd));
        verify(transactionRepository, never()).save(any());
    }

    @Test
    void create_shouldThrowValidation_whenAmountZero() {
        when(currentUserProvider.currentUserId()).thenReturn(USER_1);

        UUID walletUuid = UUID.randomUUID();
        WalletId walletId = new WalletId(walletUuid);
        when(walletQueryPort.exists(USER_1, walletId)).thenReturn(true);
        when(walletQueryPort.getCurrencyId(USER_1, walletId)).thenReturn(new CurrencyId("RUB"));

        CreateTransactionCommand cmd = new CreateTransactionCommand(
                walletUuid.toString(),
                null,
                "EXPENSE",
                BigDecimal.ZERO,
                Instant.parse("2025-01-01T10:00:00Z")
        );

        assertThrows(ValidationException.class, () -> service.create(cmd));
        verify(transactionRepository, never()).save(any());
    }

    @Test
    void create_shouldThrowValidation_whenTypeUnknown() {
        when(currentUserProvider.currentUserId()).thenReturn(USER_1);

        UUID walletUuid = UUID.randomUUID();
        WalletId walletId = new WalletId(walletUuid);
        when(walletQueryPort.exists(USER_1, walletId)).thenReturn(true);
        when(walletQueryPort.getCurrencyId(USER_1, walletId)).thenReturn(new CurrencyId("RUB"));

        CreateTransactionCommand cmd = new CreateTransactionCommand(
                walletUuid.toString(),
                null,
                "SOMETHING",
                new BigDecimal("10"),
                Instant.parse("2025-01-01T10:00:00Z")
        );

        assertThrows(ValidationException.class, () -> service.create(cmd));
        verify(transactionRepository, never()).save(any());
    }

    @Test
    void create_shouldThrowValidation_whenTypeNull() {
        when(currentUserProvider.currentUserId()).thenReturn(USER_1);

        UUID walletUuid = UUID.randomUUID();
        WalletId walletId = new WalletId(walletUuid);
        when(walletQueryPort.exists(USER_1, walletId)).thenReturn(true);
        when(walletQueryPort.getCurrencyId(USER_1, walletId)).thenReturn(new CurrencyId("RUB"));

        CreateTransactionCommand cmd = new CreateTransactionCommand(
                walletUuid.toString(),
                null,
                null,
                new BigDecimal("10"),
                Instant.parse("2025-01-01T10:00:00Z")
        );

        assertThrows(ValidationException.class, () -> service.create(cmd));
        verify(transactionRepository, never()).save(any());
    }
}
