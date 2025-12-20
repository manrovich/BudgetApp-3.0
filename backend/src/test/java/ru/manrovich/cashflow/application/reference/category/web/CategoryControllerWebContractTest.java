package ru.manrovich.cashflow.application.reference.category.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.manrovich.cashflow.application.common.web.TraceIdFilter;
import ru.manrovich.cashflow.application.reference.category.usecase.create.CreateCategoryCommand;
import ru.manrovich.cashflow.application.reference.category.usecase.create.CreateCategoryResult;
import ru.manrovich.cashflow.application.reference.category.usecase.create.CreateCategoryUseCase;
import ru.manrovich.cashflow.application.reference.category.web.create.CreateCategoryHandler;
import ru.manrovich.cashflow.application.reference.category.web.create.CreateCategoryRequest;
import ru.manrovich.cashflow.domain.kernel.exception.ConflictException;
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

@WebMvcTest(controllers = CategoryController.class)
@Import({CreateCategoryHandler.class})
class CategoryControllerWebContractTest extends WebContractTestBase {

    @Autowired
    MockMvc mvc;
    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    CreateCategoryUseCase useCase;

    @Test
    void create_shouldReturn200_whenOk_andSetTraceHeader() throws Exception {
        when(useCase.execute(any(CreateCategoryCommand.class)))
                .thenReturn(new CreateCategoryResult("11111111-1111-1111-1111-111111111111", "Food"));

        CreateCategoryRequest request = new CreateCategoryRequest("Food");

        mvc.perform(post("/api/categories")
                        .header(TraceIdFilter.TRACE_ID_HEADER, "req-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(header().string(TraceIdFilter.TRACE_ID_HEADER, "req-1"))
                .andExpect(jsonPath("$.id").value("11111111-1111-1111-1111-111111111111"))
                .andExpect(jsonPath("$.name").value("Food"));

        verify(useCase).execute(new CreateCategoryCommand("Food"));
    }

    @Test
    void create_shouldReturn409_whenUseCaseThrowsConflict_andTraceIdInBodyMatchesHeader() throws Exception {
        when(useCase.execute(any(CreateCategoryCommand.class)))
                .thenThrow(new ConflictException("Category with name already exists"));

        CreateCategoryRequest request = new CreateCategoryRequest("Food");

        mvc.perform(post("/api/categories")
                        .header(TraceIdFilter.TRACE_ID_HEADER, "req-409")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(header().string(TraceIdFilter.TRACE_ID_HEADER, "req-409"))
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.path").value("/api/categories"))
                .andExpect(jsonPath("$.traceId").value("req-409"));

        verify(useCase).execute(new CreateCategoryCommand("Food"));
    }

    @Test
    void create_shouldReturn400_whenUseCaseThrowsValidation_andTraceIdInBodyMatchesHeader() throws Exception {
        when(useCase.execute(any(CreateCategoryCommand.class)))
                .thenThrow(new ValidationException("Category name must not be blank"));

        CreateCategoryRequest request = new CreateCategoryRequest("   ");

        mvc.perform(post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(header().string(TraceIdFilter.TRACE_ID_HEADER, not(blankOrNullString())))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.path").value("/api/categories"));
    }
}
