package com.tomlisow.githubapi.exception;

import com.tomlisow.githubapi.model.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
@Slf4j
public class HttpExceptionHandler extends ResponseEntityExceptionHandler {

    public static final String EXCEPTION_WAS_THROWN_MESSAGE = "Exception {} was thrown.";
    public static final String NON_EXISTENT_USERNAME_MESSAGE = "Non-existent GitHub username";
    public static final String WRONG_HTTP_MEDIA_TYPE_MESSAGE = "Wrong HTTP Media Type. Acceptable representations: [application/json].";

    @ExceptionHandler(HttpClientErrorException.class)
    public ResponseEntity<ErrorResponse> handleHttpClientErrorException() {
        log.debug(EXCEPTION_WAS_THROWN_MESSAGE, HttpClientErrorException.class);
        ErrorResponse errorResponse = buildErrorResponse(HttpStatus.NOT_FOUND, NON_EXISTENT_USERNAME_MESSAGE);
        return ResponseEntity
                .status(errorResponse.status())
                .body(errorResponse);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMediaTypeNotAcceptable(HttpMediaTypeNotAcceptableException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        log.debug(EXCEPTION_WAS_THROWN_MESSAGE, HttpMediaTypeNotAcceptableException.class);
        ErrorResponse errorResponse = buildErrorResponse(HttpStatus.NOT_ACCEPTABLE, WRONG_HTTP_MEDIA_TYPE_MESSAGE);
        return ResponseEntity
                .status(errorResponse.status())
                .contentType(MediaType.APPLICATION_JSON)
                .body(errorResponse);
    }

    private ErrorResponse buildErrorResponse(HttpStatus status, String message) {
        ErrorResponse errorResponse = new ErrorResponse(
                status.value(),
                message);
        log.info("Error response: {}", errorResponse);
        return errorResponse;
    }
}
