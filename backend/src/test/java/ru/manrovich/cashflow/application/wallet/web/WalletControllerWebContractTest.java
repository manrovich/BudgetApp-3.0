package ru.manrovich.cashflow.application.wallet.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.manrovich.cashflow.application.common.web.TraceIdFilter;
import ru.manrovich.cashflow.application.wallet.usecase.create.CreateWalletCommand;
import ru.manrovich.cashflow.application.wallet.usecase.create.CreateWalletResult;
import ru.manrovich.cashflow.application.wallet.usecase.create.CreateWalletUseCase;
import ru.manrovich.cashflow.application.wallet.web.create.CreateWalletHandler;
import ru.manrovich.cashflow.application.wallet.web.create.CreateWalletRequest;
import ru.manrovich.cashflow.domain.kernel.exception.NotFoundException;
import ru.manrovich.cashflow.domain.kernel.exception.ValidationException;
import ru.manrovich.cashflow.testing.web.WebContractTestBase;

import static org.hamcrest.Matchers.blankOrNullString;
import static org.hamcrest.Matchers.not;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = WalletController.class)
@Import({CreateWalletHandler.class})
class WalletControllerWebContractTest extends WebContractTestBase {

    @Autowired
    MockMvc mvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    CreateWalletUseCase useCase;

    @Test
    void create_shouldReturn201_whenOk_andSetTraceHeader() throws Exception {
        when(useCase.execute(any(CreateWalletCommand.class)))
                .thenReturn(new CreateWalletResult(
                        "11111111-1111-1111-1111-111111111111",
                        "Main",
                        "RUB"
                ));

        CreateWalletRequest request = new CreateWalletRequest("Main", "RUB");

        mvc.perform(post("/api/wallets")
                        .header(TraceIdFilter.TRACE_ID_HEADER, "req-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(header().string(TraceIdFilter.TRACE_ID_HEADER, "req-1"))
                .andExpect(jsonPath("$.id").value("11111111-1111-1111-1111-111111111111"))
                .andExpect(jsonPath("$.name").value("Main"))
                .andExpect(jsonPath("$.currencyCode").value("RUB"));

        verify(useCase).execute(new CreateWalletCommand("Main", "RUB"));
    }

    @Test
    void create_shouldReturn404_whenUseCaseThrowsNotFound_andTraceIdInBodyMatchesHeader() throws Exception {
        when(useCase.execute(any(CreateWalletCommand.class)))
                .thenThrow(new NotFoundException("Currency not found: RUB"));

        CreateWalletRequest request = new CreateWalletRequest("Main", "RUB");

        mvc.perform(post("/api/wallets")
                        .header(TraceIdFilter.TRACE_ID_HEADER, "req-404")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(header().string(TraceIdFilter.TRACE_ID_HEADER, "req-404"))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.path").value("/api/wallets"))
                .andExpect(jsonPath("$.traceId").value("req-404"));

        verify(useCase).execute(new CreateWalletCommand("Main", "RUB"));
    }

    @Test
    void create_shouldReturn400_whenUseCaseThrowsValidation_andTraceIdInBodyMatchesHeader() throws Exception {
        when(useCase.execute(any(CreateWalletCommand.class)))
                .thenThrow(new ValidationException("Wallet name must not be blank"));

        CreateWalletRequest request = new CreateWalletRequest("   ", "RUB");

        mvc.perform(post("/api/wallets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(header().string(TraceIdFilter.TRACE_ID_HEADER, not(blankOrNullString())))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.path").value("/api/wallets"));
    }
}
