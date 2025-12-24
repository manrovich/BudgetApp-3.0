package ru.manrovich.cashflow.api.currency.admin;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.manrovich.cashflow.api.common.tracing.TraceIdFilter;
import ru.manrovich.cashflow.api.currency.admin.dto.SeedCurrenciesRequest;
import ru.manrovich.cashflow.api.currency.admin.dto.SeedCurrenciesResponse;
import ru.manrovich.cashflow.application.currency.command.CurrencyCommandService;
import ru.manrovich.cashflow.application.currency.command.SeedCurrenciesCommand;
import ru.manrovich.cashflow.application.currency.command.SeedCurrenciesResult;
import ru.manrovich.cashflow.testing.web.WebContractTestBase;

import java.util.List;

import static org.hamcrest.Matchers.blankOrNullString;
import static org.hamcrest.Matchers.not;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = CurrencyAdminController.class)
class CurrencyAdminControllerWebContractTest extends WebContractTestBase {

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper objectMapper;

    @MockitoBean CurrencyCommandService commandService;
    @MockitoBean org.springframework.context.MessageSource messageSource;

    @Test
    void seed_shouldReturn200_whenBodyAbsent_useDefaultRequest_andSetTraceHeader() throws Exception {
        when(commandService.seed(any(SeedCurrenciesCommand.class)))
                .thenReturn(new SeedCurrenciesResult(1, 2, false, List.of("USD")));

        when(messageSource.getMessage(eq("currency.seed.summary"), any(), any()))
                .thenReturn("summary");

        mvc.perform(post("/api/admin/currencies/seed")
                        .header(TraceIdFilter.TRACE_ID_HEADER, "test-trace-id")
                        .header("Accept-Language", "ru"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(header().string(TraceIdFilter.TRACE_ID_HEADER, "test-trace-id"))
                .andExpect(jsonPath("$.inserted").value(1))
                .andExpect(jsonPath("$.skipped").value(2))
                .andExpect(jsonPath("$.dryRun").value(false))
                .andExpect(jsonPath("$.insertedCodes[0]").value("USD"))
                .andExpect(jsonPath("$.summary").value("summary"));

        verify(commandService).seed(new SeedCurrenciesCommand(false));
    }

    @Test
    void seed_shouldReturn200_whenBodyProvided_andPassDryRunTrue() throws Exception {
        when(commandService.seed(any(SeedCurrenciesCommand.class)))
                .thenReturn(new SeedCurrenciesResult(0, 3, true, List.of()));

        when(messageSource.getMessage(eq("currency.seed.summary"), any(), any()))
                .thenReturn("summary");

        SeedCurrenciesRequest request = new SeedCurrenciesRequest(true);

        mvc.perform(post("/api/admin/currencies/seed")
                        .header("Accept-Language", "en")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(header().string(TraceIdFilter.TRACE_ID_HEADER, not(blankOrNullString())))
                .andExpect(jsonPath("$.dryRun").value(true));

        verify(commandService).seed(new SeedCurrenciesCommand(true));
    }
}
