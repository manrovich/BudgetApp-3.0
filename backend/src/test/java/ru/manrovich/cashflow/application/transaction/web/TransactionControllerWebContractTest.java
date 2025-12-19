package ru.manrovich.cashflow.application.transaction.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.manrovich.cashflow.application.common.web.TraceIdFilter;
import ru.manrovich.cashflow.application.transaction.usecase.create.CreateTransactionCommand;
import ru.manrovich.cashflow.application.transaction.usecase.create.CreateTransactionResult;
import ru.manrovich.cashflow.application.transaction.usecase.create.CreateTransactionUseCase;
import ru.manrovich.cashflow.application.transaction.web.create.CreateTransactionHandler;
import ru.manrovich.cashflow.application.transaction.web.create.CreateTransactionRequest;
import ru.manrovich.cashflow.domain.kernel.exception.NotFoundException;
import ru.manrovich.cashflow.domain.kernel.exception.ValidationException;
import ru.manrovich.cashflow.testing.web.WebContractTestBase;

import java.time.Instant;

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

@WebMvcTest(controllers = TransactionController.class)
@Import({CreateTransactionHandler.class})
class TransactionControllerWebContractTest extends WebContractTestBase {

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper objectMapper;

    @MockitoBean
    CreateTransactionUseCase useCase;

    @Test
    void create_shouldReturn201_whenOk_andSetTraceHeader() throws Exception {
        when(useCase.execute(any(CreateTransactionCommand.class)))
                .thenReturn(new CreateTransactionResult("11111111-1111-1111-1111-111111111111"));

        CreateTransactionRequest request = new CreateTransactionRequest(
                "22222222-2222-2222-2222-222222222222",
                null,
                "100.00",
                Instant.parse("2025-01-01T10:00:00Z")
        );

        mvc.perform(post("/api/transactions")
                        .header(TraceIdFilter.TRACE_ID_HEADER, "req-tx-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(header().string(TraceIdFilter.TRACE_ID_HEADER, "req-tx-1"))
                .andExpect(jsonPath("$.id").value("11111111-1111-1111-1111-111111111111"));

        verify(useCase).execute(new CreateTransactionCommand(
                "22222222-2222-2222-2222-222222222222",
                null,
                "100.00",
                Instant.parse("2025-01-01T10:00:00Z")
        ));
    }

    @Test
    void create_shouldReturn404_whenUseCaseThrowsNotFound_andTraceIdMatchesHeader() throws Exception {
        when(useCase.execute(any(CreateTransactionCommand.class)))
                .thenThrow(new NotFoundException("Wallet not found"));

        CreateTransactionRequest request = new CreateTransactionRequest(
                "22222222-2222-2222-2222-222222222222",
                null,
                "100.00",
                Instant.parse("2025-01-01T10:00:00Z")
        );

        mvc.perform(post("/api/transactions")
                        .header(TraceIdFilter.TRACE_ID_HEADER, "req-tx-404")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(header().string(TraceIdFilter.TRACE_ID_HEADER, "req-tx-404"))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.path").value("/api/transactions"))
                .andExpect(jsonPath("$.traceId").value("req-tx-404"));

        verify(useCase).execute(any(CreateTransactionCommand.class));
    }

    @Test
    void create_shouldReturn400_whenUseCaseThrowsValidation_andTraceIdGenerated() throws Exception {
        when(useCase.execute(any(CreateTransactionCommand.class)))
                .thenThrow(new ValidationException("Money amount must not be zero"));

        CreateTransactionRequest request = new CreateTransactionRequest(
                "22222222-2222-2222-2222-222222222222",
                null,
                "0",
                Instant.parse("2025-01-01T10:00:00Z")
        );

        mvc.perform(post("/api/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(header().string(TraceIdFilter.TRACE_ID_HEADER, not(blankOrNullString())))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.path").value("/api/transactions"));
    }
}
