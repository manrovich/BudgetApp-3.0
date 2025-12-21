package ru.manrovich.cashflow.application.reference.currency.usecase;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.manrovich.cashflow.application.reference.currency.service.CurrencyApplicationService;
import ru.manrovich.cashflow.application.reference.currency.usecase.query.ListCurrenciesQuery;
import ru.manrovich.cashflow.domain.reference.currency.port.CurrencyQueryPort;
import ru.manrovich.cashflow.domain.reference.currency.port.CurrencyRepository;
import ru.manrovich.cashflow.shared.query.Slice;
import ru.manrovich.cashflow.shared.query.SlicePage;
import ru.manrovich.cashflow.shared.readmodel.CurrencyListItem;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CurrencyApplicationServiceMockTest {
    @Mock
    CurrencyQueryPort currencyQueryPort;
    @Mock
    CurrencyRepository currencyRepository;

    @InjectMocks
    CurrencyApplicationService service;

    @Test
    void list_shouldUseDefaults_whenPageAndSizeNull() {
        Slice<CurrencyListItem> expected = new Slice<>(
                List.of(),
                new SlicePage(0, 200, false)
        );

        when(currencyQueryPort.findListItems(any(CurrencyQueryPort.CurrencySearchCriteria.class)))
                .thenReturn(expected);

        ListCurrenciesQuery query = new ListCurrenciesQuery(
                null,
                null,
                null
        );

        Slice<CurrencyListItem> actual = service.list(query);

        assertSame(expected, actual);

        ArgumentCaptor<CurrencyQueryPort.CurrencySearchCriteria> captor =
                ArgumentCaptor.forClass(CurrencyQueryPort.CurrencySearchCriteria.class);

        verify(currencyQueryPort).findListItems(captor.capture());

        CurrencyQueryPort.CurrencySearchCriteria criteria = captor.getValue();
        assertNull(criteria.query());
        assertEquals(0, criteria.page());
        assertEquals(200, criteria.size());
    }

    @Test
    void list_shouldClampPageAndSize() {
        Slice<CurrencyListItem> expected = new Slice<>(
                List.of(),
                new SlicePage(0, 200, false)
        );

        when(currencyQueryPort.findListItems(any(CurrencyQueryPort.CurrencySearchCriteria.class)))
                .thenReturn(expected);

        ListCurrenciesQuery query = new ListCurrenciesQuery(
                "rub",
                -100,
                10_000
        );

        service.list(query);

        ArgumentCaptor<CurrencyQueryPort.CurrencySearchCriteria> captor =
                ArgumentCaptor.forClass(CurrencyQueryPort.CurrencySearchCriteria.class);

        verify(currencyQueryPort).findListItems(captor.capture());

        CurrencyQueryPort.CurrencySearchCriteria criteria = captor.getValue();
        assertEquals("rub", criteria.query());
        assertEquals(0, criteria.page());
        assertEquals(200, criteria.size());
    }
}
