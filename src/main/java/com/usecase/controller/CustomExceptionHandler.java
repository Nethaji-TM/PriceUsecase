package com.usecase.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.usecase.handler.ErrorResponse;
import com.usecase.handler.PriceNotFoundException;

@RestControllerAdvice
public class CustomExceptionHandler {

    @ExceptionHandler(PriceNotFoundException.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public ResponseEntity<ErrorResponse> handlePriceNotFoundException(PriceNotFoundException e) {
        ErrorResponse errorResponse = new ErrorResponse(
                "Not_Found",
                "Unavailable prices",
                e.getHttpStatus().value(),
                e.getMessage()
        );
        return new ResponseEntity<>(errorResponse, e.getHttpStatus());
    }
}