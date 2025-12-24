package ru.manrovich.cashflow.api.wallet;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.manrovich.cashflow.api.wallet.dto.CreateWalletRequest;
import ru.manrovich.cashflow.api.wallet.dto.CreateWalletResponse;
import ru.manrovich.cashflow.application.wallet.command.CreateWalletCommand;
import ru.manrovich.cashflow.application.wallet.command.CreateWalletResult;
import ru.manrovich.cashflow.application.wallet.command.WalletCommandService;
import ru.manrovich.cashflow.application.wallet.query.WalletQueryService;
import ru.manrovich.cashflow.domain.kernel.exception.NotFoundException;
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

@WebMvcTest(controllers = WalletController.class)
class WalletControllerWebContractTest extends WebContractTestBase {

    @Autowired
    MockMvc mvc;
    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    WalletCommandService commandService;
    @MockitoBean
    WalletQueryService queryService;

    @Test
    void create_shouldReturn201_andResponseBody_whenOk() throws Exception {
        when(commandService.create(any(CreateWalletCommand.class)))
                .thenReturn(new CreateWalletResult(
                        "11111111-1111-1111-1111-111111111111",
                        "Main",
                        "RUB"
                ));

        CreateWalletRequest request = new CreateWalletRequest("Main", "RUB");

        mvc.perform(post("/api/wallets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value("11111111-1111-1111-1111-111111111111"))
                .andExpect(jsonPath("$.name").value("Main"))
                .andExpect(jsonPath("$.currencyCode").value("RUB"));

        verify(commandService).create(new CreateWalletCommand("Main", "RUB"));
    }

    @Test
    void create_shouldReturn400_whenRequestInvalid_andNotCallUseCase() throws Exception {
        CreateWalletRequest request = new CreateWalletRequest("Main", "RU");

        mvc.perform(post("/api/wallets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.fieldErrors", hasSize(1)))
                .andExpect(jsonPath("$.fieldErrors[*].field", hasItem("currencyCode")));

        verifyNoInteractions(commandService);
    }

    @Test
    void create_shouldReturn404_whenUseCaseThrowsNotFound() throws Exception {
        when(commandService.create(any(CreateWalletCommand.class)))
                .thenThrow(new NotFoundException("Currency not found"));

        CreateWalletRequest request = new CreateWalletRequest("Main", "RUB");

        mvc.perform(post("/api/wallets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.fieldErrors", hasSize(0)));

        verify(commandService).create(new CreateWalletCommand("Main", "RUB"));
    }
}
