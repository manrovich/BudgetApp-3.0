package ru.manrovich.cashflow.api.common.error;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

public record ApiErrorResponse(
        Instant timestamp,
        int status,
        String error,
        String message,
        String path,
        String traceId,
        List<ApiFieldError> fieldErrors
) {

    public static ApiErrorResponse of(
            Instant timestamp,
            int status,
            String error,
            String message,
            String path,
            String traceId,
            List<ApiFieldError> fieldErrors
    ) {
        return new ApiErrorResponse(timestamp, status, error, message, path, traceId, fieldErrors);
    }

    public static ApiErrorResponse of(
            Instant timestamp,
            int status,
            String error,
            String message,
            String path,
            String traceId
    ) {
        return new ApiErrorResponse(timestamp, status, error, message, path, traceId, Collections.emptyList());
    }
}
