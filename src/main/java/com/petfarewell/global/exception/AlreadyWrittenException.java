package com.petfarewell.global.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT) // 409
public class AlreadyWrittenException extends RuntimeException{
    public AlreadyWrittenException(String message) {
        super(message);
    }
}
