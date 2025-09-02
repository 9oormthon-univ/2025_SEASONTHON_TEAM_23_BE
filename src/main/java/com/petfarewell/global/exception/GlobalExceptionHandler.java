package com.petfarewell.global.exception;

import com.petfarewell.global.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AlreadyWrittenException.class)
    public ResponseEntity<ErrorResponse> handleAlreadyWritten(AlreadyWrittenException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErrorResponse("ALREADY_WRITTEN", ex.getMessage()));
    }

}
