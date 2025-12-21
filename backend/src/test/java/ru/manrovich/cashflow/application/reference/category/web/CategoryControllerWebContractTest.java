package ru.manrovich.cashflow.application.reference.category.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.manrovich.cashflow.application.reference.category.usecase.CategoryUseCase;
import ru.manrovich.cashflow.application.reference.category.usecase.command.CreateCategoryCommand;
import ru.manrovich.cashflow.application.reference.category.usecase.result.CreateCategoryResult;
import ru.manrovich.cashflow.application.reference.category.web.dto.CreateCategoryRequest;
import ru.manrovich.cashflow.application.reference.category.web.mapper.CategoryWebMapper;
import ru.manrovich.cashflow.domain.kernel.exception.ConflictException;
import ru.manrovich.cashflow.testing.web.WebContractTestBase;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = CategoryController.class)
@Import(CategoryWebMapper.class)
class CategoryControllerWebContractTest extends WebContractTestBase {

    @Autowired
    MockMvc mvc;
    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    CategoryUseCase useCase;

    @Test
    void create_shouldReturn201_andResponseBody_whenOk() throws Exception {
        when(useCase.create(any(CreateCategoryCommand.class)))
                .thenReturn(new CreateCategoryResult(
                        "11111111-1111-1111-1111-111111111111",
                        "Food"
                ));

        CreateCategoryRequest request = new CreateCategoryRequest("Food");

        mvc.perform(post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value("11111111-1111-1111-1111-111111111111"))
                .andExpect(jsonPath("$.name").value("Food"));

        verify(useCase).create(new CreateCategoryCommand("Food"));
    }

    @Test
    void create_shouldReturn400_whenRequestInvalid_andNotCallUseCase() throws Exception {
        // @NotBlank нарушен
        CreateCategoryRequest request = new CreateCategoryRequest("   ");

        mvc.perform(post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                // общий контракт ошибок: fieldErrors есть всегда
                .andExpect(jsonPath("$.fieldErrors", hasSize(1)))
                .andExpect(jsonPath("$.fieldErrors[*].field", hasItem("name")));

        verifyNoInteractions(useCase);
    }

    @Test
    void create_shouldReturn409_whenUseCaseThrowsConflict() throws Exception {
        when(useCase.create(any(CreateCategoryCommand.class)))
                .thenThrow(new ConflictException("Category with name already exists"));

        CreateCategoryRequest request = new CreateCategoryRequest("Food");

        mvc.perform(post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.fieldErrors", hasSize(0)));

        verify(useCase).create(new CreateCategoryCommand("Food"));
    }
}
