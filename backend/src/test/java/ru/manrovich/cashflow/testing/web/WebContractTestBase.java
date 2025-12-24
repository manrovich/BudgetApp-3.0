package ru.manrovich.cashflow.testing.web;

import org.springframework.context.annotation.Import;
import ru.manrovich.cashflow.api.common.error.RestExceptionHandler;
import ru.manrovich.cashflow.api.common.tracing.TraceIdFilter;

@Import({
        RestExceptionHandler.class,
        TraceIdFilter.class,
        TestSecurityConfig.class
})
public abstract class WebContractTestBase {
}
