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

import java.math.BigDecimal;
import java.time.Instant;

import static org.hamcrest.Matchers.blankOrNullString;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = TransactionController.class)
@Import(CreateTransactionHandler.class)
class TransactionControllerWebContractTest extends WebContractTestBase {

    @Autowired
    MockMvc mvc;
    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    CreateTransactionUseCase useCase;

    @Test
    void create_shouldReturn201_whenOk_andSetTraceHeader() throws Exception {
        when(useCase.execute(any(CreateTransactionCommand.class)))
                .thenReturn(new CreateTransactionResult("11111111-1111-1111-1111-111111111111"));

        Instant occurredAt = Instant.parse("2025-01-01T10:00:00Z");
        BigDecimal amount = new BigDecimal("100.00");

        CreateTransactionRequest request = new CreateTransactionRequest(
                "22222222-2222-2222-2222-222222222222",
                null,
                "EXPENSE",
                amount,
                occurredAt
        );

        mvc.perform(post("/api/transactions")
                        .header(TraceIdFilter.TRACE_ID_HEADER, "req-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(header().string(TraceIdFilter.TRACE_ID_HEADER, "req-1"))
                .andExpect(jsonPath("$.id").value("11111111-1111-1111-1111-111111111111"));

        verify(useCase).execute(new CreateTransactionCommand(
                "22222222-2222-2222-2222-222222222222",
                null,
                "EXPENSE",
                amount,
                occurredAt
        ));
    }

    @Test
    void create_shouldReturn400_whenRequestInvalid_andNotCallUseCase() throws Exception {
        // walletId нарушает @UUID
        Instant occurredAt = Instant.parse("2025-01-01T10:00:00Z");
        BigDecimal amount = new BigDecimal("100.00");

        CreateTransactionRequest request = new CreateTransactionRequest(
                "not-a-uuid",
                null,
                "INCOME",
                amount,
                occurredAt
        );

        mvc.perform(post("/api/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.fieldErrors", hasSize(1)))
                .andExpect(jsonPath("$.fieldErrors[*].field", hasItem("walletId")));

        verifyNoInteractions(useCase);
    }

    @Test
    void create_shouldReturn404_whenUseCaseThrowsNotFound() throws Exception {
        when(useCase.execute(any(CreateTransactionCommand.class)))
                .thenThrow(new NotFoundException("Wallet not found"));

        Instant occurredAt = Instant.parse("2025-01-01T10:00:00Z");
        BigDecimal amount = new BigDecimal("100.00");

        CreateTransactionRequest request = new CreateTransactionRequest(
                "22222222-2222-2222-2222-222222222222",
                null,
                "INCOME",
                amount,
                occurredAt
        );

        mvc.perform(post("/api/transactions")
                        .header(TraceIdFilter.TRACE_ID_HEADER, "req-404")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(header().string(TraceIdFilter.TRACE_ID_HEADER, "req-404"))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.fieldErrors", hasSize(0)))
                .andExpect(jsonPath("$.traceId").value("req-404"));

        verify(useCase).execute(new CreateTransactionCommand(
                "22222222-2222-2222-2222-222222222222",
                null,
                "INCOME",
                amount,
                occurredAt
        ));
    }

    @Test
    void create_shouldReturn400_whenUseCaseThrowsDomainValidation() throws Exception {
        when(useCase.execute(any(CreateTransactionCommand.class)))
                .thenThrow(new ValidationException("Transaction amount must be > 0"));

        Instant occurredAt = Instant.parse("2025-01-01T10:00:00Z");
        BigDecimal amount = new BigDecimal("0");

        CreateTransactionRequest request = new CreateTransactionRequest(
                "22222222-2222-2222-2222-222222222222",
                null,
                "EXPENSE",
                amount,
                occurredAt
        );

        mvc.perform(post("/api/transactions")
                        .header(TraceIdFilter.TRACE_ID_HEADER, "req-400")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(header().string(TraceIdFilter.TRACE_ID_HEADER, "req-400"))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.fieldErrors", hasSize(0)))
                .andExpect(jsonPath("$.traceId").value("req-400"));

        verify(useCase).execute(new CreateTransactionCommand(
                "22222222-2222-2222-2222-222222222222",
                null,
                "EXPENSE",
                amount,
                occurredAt
        ));
    }

    @Test
    void create_shouldReturn400_whenNoTraceHeaderProvided_andServerGeneratesTraceId() throws Exception {
        when(useCase.execute(any(CreateTransactionCommand.class)))
                .thenReturn(new CreateTransactionResult("11111111-1111-1111-1111-111111111111"));

        Instant occurredAt = Instant.parse("2025-01-01T10:00:00Z");
        BigDecimal amount = new BigDecimal("100.00");

        CreateTransactionRequest request = new CreateTransactionRequest(
                "22222222-2222-2222-2222-222222222222",
                null,
                "INCOME",
                amount,
                occurredAt
        );

        mvc.perform(post("/api/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(header().string(TraceIdFilter.TRACE_ID_HEADER, not(blankOrNullString())));

        verify(useCase).execute(new CreateTransactionCommand(
                "22222222-2222-2222-2222-222222222222",
                null,
                "INCOME",
                amount,
                occurredAt
        ));
    }
}
