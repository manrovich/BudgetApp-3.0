package ru.manrovich.cashflow.application.common.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.manrovich.cashflow.application.reference.category.web.dto.CreateCategoryRequest;
import ru.manrovich.cashflow.application.transaction.web.dto.CreateTransactionRequest;
import ru.manrovich.cashflow.domain.kernel.exception.NotFoundException;
import ru.manrovich.cashflow.testing.web.WebContractTestBase;

import static org.hamcrest.Matchers.blankOrNullString;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = WebInfrastructureContractTest.TestController.class)
@Import(WebInfrastructureContractTest.TestController.class)
class WebInfrastructureContractTest extends WebContractTestBase {

    @Autowired
    MockMvc mvc;
    @Autowired ObjectMapper objectMapper;

    @MockitoBean DummyUseCase dummyUseCase;

    @Test
    void traceIdFilter_shouldEchoHeader_whenProvided() throws Exception {
        mvc.perform(get("/__test/ok")
                        .header(TraceIdFilter.TRACE_ID_HEADER, "req-1"))
                .andExpect(status().isOk())
                .andExpect(header().string(TraceIdFilter.TRACE_ID_HEADER, "req-1"));
    }

    @Test
    void validation_shouldReturn400_withFieldErrors_andNotCallControllerLogic() throws Exception {
        CreateCategoryRequest request = new CreateCategoryRequest("   ");

        mvc.perform(post("/__test/category")
                        .header(TraceIdFilter.TRACE_ID_HEADER, "req-val-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(header().string(TraceIdFilter.TRACE_ID_HEADER, "req-val-1"))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.path").value("/__test/category"))
                .andExpect(jsonPath("$.traceId").value("req-val-1"))
                .andExpect(jsonPath("$.fieldErrors", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$.fieldErrors[0].field", not(blankOrNullString())));

        // если валидация упала — контроллерный код не должен быть вызван
        verifyNoInteractions(dummyUseCase);
    }

    @Test
    void malformedBody_shouldReturn400_andStableMessage() throws Exception {
        String body = """
                {
                  \"walletId\": \"22222222-2222-2222-2222-222222222222\",
                  \"categoryId\": null,
                  \"amount\": 100.00,
                  \"occurredAt\": \"not-an-instant\"
                }
                """;

        mvc.perform(post("/__test/tx")
                        .header(TraceIdFilter.TRACE_ID_HEADER, "req-bad-json-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(header().string(TraceIdFilter.TRACE_ID_HEADER, "req-bad-json-1"))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.path").value("/__test/tx"))
                .andExpect(jsonPath("$.traceId").value("req-bad-json-1"))
                .andExpect(jsonPath("$.message").value("Malformed request body"));
    }

    @Test
    void domainExceptionMapping_shouldKeepTraceIdSameAsHeader() throws Exception {
        mvc.perform(get("/__test/not-found")
                        .header(TraceIdFilter.TRACE_ID_HEADER, "req-nf-1"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(header().string(TraceIdFilter.TRACE_ID_HEADER, "req-nf-1"))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.traceId").value("req-nf-1"));
    }

    interface DummyUseCase {
        void run();
    }

    @RestController
    @RequestMapping("/__test")
    static class TestController {

        private final DummyUseCase dummyUseCase;

        TestController(DummyUseCase dummyUseCase) {
            this.dummyUseCase = dummyUseCase;
        }

        @GetMapping("/ok")
        void ok() { }

        @GetMapping("/not-found")
        void notFound() {
            throw new NotFoundException("not found");
        }

        @PostMapping("/category")
        void createCategory(@Valid @RequestBody CreateCategoryRequest request) {
            dummyUseCase.run();
        }

        @PostMapping("/tx")
        void createTx(@Valid @RequestBody CreateTransactionRequest request) {
            dummyUseCase.run();
        }
    }
}
