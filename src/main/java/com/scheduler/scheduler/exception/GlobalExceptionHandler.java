package com.scheduler.scheduler.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handlerRuntimeException(RuntimeException e) {
        return ResponseEntity.status(404).body(e.getMessage());
    }
    @ExceptionHandler(ExistingUserException.class)
    public ResponseEntity<String> handlerExistingUserException(ExistingUserException e) {
        return ResponseEntity.status(404).body(e.getMessage());
    }
}
