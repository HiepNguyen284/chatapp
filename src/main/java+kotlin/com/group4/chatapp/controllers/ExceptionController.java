package com.group4.chatapp.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import com.group4.chatapp.exceptions.ApiException;

@RestControllerAdvice
public class ExceptionController extends ResponseEntityExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExceptionController.class);

    @ExceptionHandler(UnsupportedOperationException.class)
    public ApiException handleUnsupportedOperation(UnsupportedOperationException ex) {
        LOGGER.warn("Unsupported operation: {}", ex.getMessage());
        ApiException apiException = new ApiException(
            HttpStatus.NOT_IMPLEMENTED,
            "Feature not available"
        );
        apiException.setDetail(ex.getMessage());
        return apiException;
    }
}
