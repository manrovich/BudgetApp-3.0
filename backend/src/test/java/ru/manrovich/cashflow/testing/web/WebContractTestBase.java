package ru.manrovich.cashflow.testing.web;

import org.springframework.context.annotation.Import;
import ru.manrovich.cashflow.application.common.web.RestExceptionHandler;
import ru.manrovich.cashflow.application.common.web.TraceIdFilter;

@Import({
        RestExceptionHandler.class,
        TraceIdFilter.class,
        TestSecurityConfig.class
})
public abstract class WebContractTestBase {
}