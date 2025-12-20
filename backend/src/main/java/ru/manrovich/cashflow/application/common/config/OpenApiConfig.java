package ru.manrovich.cashflow.application.common.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    OpenAPI cashflowOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Cashflow API")
                        .version("v1")
                        .description("Backend API for Cashflow application"));
    }
}
