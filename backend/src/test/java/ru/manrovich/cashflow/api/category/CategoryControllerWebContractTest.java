package ru.manrovich.cashflow.api.category;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.manrovich.cashflow.api.category.dto.CreateCategoryRequest;
import ru.manrovich.cashflow.api.category.dto.CreateCategoryResponse;
import ru.manrovich.cashflow.application.category.command.CreateCategoryCommand;
import ru.manrovich.cashflow.application.category.command.CreateCategoryResult;
import ru.manrovich.cashflow.application.category.command.CategoryCommandService;
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
class CategoryControllerWebContractTest extends WebContractTestBase {

    @Autowired
    MockMvc mvc;
    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    CategoryCommandService commandService;

    @Test
    void create_shouldReturn201_andResponseBody_whenOk() throws Exception {
        when(commandService.create(any(CreateCategoryCommand.class)))
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

        verify(commandService).create(new CreateCategoryCommand("Food"));
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

        verifyNoInteractions(commandService);
    }

    @Test
    void create_shouldReturn409_whenUseCaseThrowsConflict() throws Exception {
        when(commandService.create(any(CreateCategoryCommand.class)))
                .thenThrow(new ConflictException("Category with name already exists"));

        CreateCategoryRequest request = new CreateCategoryRequest("Food");

        mvc.perform(post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.fieldErrors", hasSize(0)));

        verify(commandService).create(new CreateCategoryCommand("Food"));
    }
}
