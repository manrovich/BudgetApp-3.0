package ru.manrovich.cashflow.application.wallet.command;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.manrovich.cashflow.application.common.security.CurrentUserProvider;
import ru.manrovich.cashflow.application.wallet.command.CreateWalletCommand;
import ru.manrovich.cashflow.application.wallet.command.CreateWalletResult;
import ru.manrovich.cashflow.domain.kernel.exception.NotFoundException;
import ru.manrovich.cashflow.domain.kernel.id.CurrencyId;
import ru.manrovich.cashflow.domain.reference.currency.port.CurrencyQueryPort;
import ru.manrovich.cashflow.domain.wallet.model.Wallet;
import ru.manrovich.cashflow.domain.wallet.port.WalletRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static ru.manrovich.cashflow.testing.data.TestUsers.USER_1;

@ExtendWith(MockitoExtension.class)
class WalletCommandServiceTest {

    @Mock
    WalletRepository walletRepository;
    @Mock
    CurrencyQueryPort currencyQueryPort;
    @Mock
    CurrentUserProvider currentUserProvider;

    @InjectMocks
    WalletCommandService service;

    @Test
    void shouldCreateWalletWhenCurrencyExists() {
        when(currentUserProvider.currentUserId()).thenReturn(USER_1);
        when(currencyQueryPort.exists(new CurrencyId("RUB"))).thenReturn(true);

        ArgumentCaptor<Wallet> captor = ArgumentCaptor.forClass(Wallet.class);
        when(walletRepository.save(captor.capture())).thenAnswer(inv -> inv.getArgument(0));

        CreateWalletResult result = service.create(new CreateWalletCommand("Main", "RUB"));

        Wallet saved = captor.getValue();
        assertEquals(USER_1.value(), saved.ownerId().value());
        assertEquals(saved.id().value().toString(), result.id());
        assertEquals("Main", result.name());
        assertEquals("RUB", result.currencyCode());

        verify(walletRepository, times(1)).save(any());
    }

    @Test
    void shouldThrowNotFoundWhenCurrencyMissing() {
        when(currentUserProvider.currentUserId()).thenReturn(USER_1);
        when(currencyQueryPort.exists(new CurrencyId("RUB"))).thenReturn(false);

        assertThrows(NotFoundException.class, () -> service.create(new CreateWalletCommand("Main", "RUB")));
        verify(walletRepository, never()).save(any());
    }
}
