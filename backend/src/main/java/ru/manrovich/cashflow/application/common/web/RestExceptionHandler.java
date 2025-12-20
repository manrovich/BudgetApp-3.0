package ru.manrovich.cashflow.application.common.web;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.manrovich.cashflow.application.common.web.dto.ApiErrorResponse;
import ru.manrovich.cashflow.application.common.web.dto.ApiFieldError;
import ru.manrovich.cashflow.domain.kernel.exception.ConflictException;
import ru.manrovich.cashflow.domain.kernel.exception.DomainException;
import ru.manrovich.cashflow.domain.kernel.exception.NotFoundException;
import ru.manrovich.cashflow.domain.kernel.exception.ValidationException;

import java.time.Instant;
import java.util.List;

@RestControllerAdvice
@Slf4j
@SuppressWarnings("unused")
public class RestExceptionHandler {

    private static final String DEFAULT_INTERNAL_ERROR_MESSAGE = "Unexpected error";
    private static final String VALIDATION_FAILED_MESSAGE = "Validation failed";
    private static final String MALFORMED_BODY_MESSAGE = "Malformed request body";

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleBodyValidation(MethodArgumentNotValidException ex,
                                                                 HttpServletRequest request) {

        List<ApiFieldError> fieldErrors = ex.getBindingResult().getFieldErrors().stream()
                .map(this::toApiFieldError)
                .toList();

        return build(HttpStatus.BAD_REQUEST, VALIDATION_FAILED_MESSAGE, request, false, fieldErrors, ex);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiErrorResponse> handleParamsValidation(ConstraintViolationException ex,
                                                                   HttpServletRequest request) {

        List<ApiFieldError> fieldErrors = ex.getConstraintViolations().stream()
                .map(v -> new ApiFieldError(
                        v.getPropertyPath().toString(),
                        v.getMessage()
                ))
                .toList();

        return build(HttpStatus.BAD_REQUEST, VALIDATION_FAILED_MESSAGE, request, false, fieldErrors, ex);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiErrorResponse> handleNotReadable(HttpMessageNotReadableException ex,
                                                              HttpServletRequest request) {
        return build(HttpStatus.BAD_REQUEST, MALFORMED_BODY_MESSAGE, request, false, List.of(), ex);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ApiErrorResponse> handleValidation(ValidationException ex, HttpServletRequest request) {
        return build(HttpStatus.BAD_REQUEST, ex.getMessage(), request, false, List.of(), ex);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleNotFound(NotFoundException ex, HttpServletRequest request) {
        return build(HttpStatus.NOT_FOUND, ex.getMessage(), request, false, List.of(), ex);
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ApiErrorResponse> handleConflict(ConflictException ex, HttpServletRequest request) {
        return build(HttpStatus.CONFLICT, ex.getMessage(), request, false, List.of(), ex);
    }

    @ExceptionHandler(DomainException.class)
    public ResponseEntity<ApiErrorResponse> handleDomain(DomainException ex, HttpServletRequest request) {
        // Доменные исключения, которые не промаплены в 4xx — это баг/неучтённый кейс.
        return build(HttpStatus.INTERNAL_SERVER_ERROR, DEFAULT_INTERNAL_ERROR_MESSAGE, request, true, List.of(), ex);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleUnknown(Exception ex, HttpServletRequest request) {
        return build(HttpStatus.INTERNAL_SERVER_ERROR, DEFAULT_INTERNAL_ERROR_MESSAGE, request, true, List.of(), ex);
    }

    private ResponseEntity<ApiErrorResponse> build(HttpStatus status,
                                                   String message,
                                                   HttpServletRequest request,
                                                   boolean logAsError,
                                                   List<ApiFieldError> fieldErrors,
                                                   Exception ex) {

        String traceId = resolveTraceId();

        ApiErrorResponse body = ApiErrorResponse.of(
                Instant.now(),
                status.value(),
                status.getReasonPhrase(),
                message,
                request.getRequestURI(),
                traceId,
                fieldErrors
        );

        if (logAsError) {
            log.error("Unhandled exception, traceId={}, status={}", traceId, status.value(), ex);
        } else {
            log.debug("Handled exception, traceId={}, status={}, message={}", traceId, status.value(), message, ex);
        }

        return ResponseEntity.status(status).body(body);
    }

    private String resolveTraceId() {
        String fromMdc = MDC.get(TraceIdFilter.TRACE_ID_KEY);
        return (fromMdc == null || fromMdc.isBlank()) ? "unknown" : fromMdc;
    }

    private ApiFieldError toApiFieldError(FieldError fe) {
        String field = fe.getField();
        String msg = fe.getDefaultMessage();
        return new ApiFieldError(field, msg == null ? "Invalid value" : msg);
    }
}
